package com.example.oderbook_gfrias.data.remote

import android.util.Log
import com.example.oderbook_gfrias.data.model.CoinbaseRequest
import okio.ByteString
import com.google.gson.GsonBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.*

@ExperimentalCoroutinesApi
class CoinbaseWebSocketListener(marketRequest: CoinbaseRequest): WebSocketListener() {

    private var requestMarket: CoinbaseRequest = marketRequest

    val socketEventChannel: Channel<SocketUpdate> = Channel()

    private val GSON = GsonBuilder().setPrettyPrinting().create()

    override fun onOpen(webSocket: WebSocket, response: Response) {
        webSocket.send(toJSON())
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        GlobalScope.launch {

            try {socketEventChannel.send(SocketUpdate(text))}
            catch (ex: java.lang.Exception){
                Log.e("Error", "Socket Closed")
            }
        }

    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        GlobalScope.launch {
            try {socketEventChannel.send(SocketUpdate(exception = SocketAbortedException()))}
            catch (ex: java.lang.Exception){
                Log.e("Error", "Socket Closed")}
        }
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        socketEventChannel.close()
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("Error", t.toString())
    }

    class SocketAbortedException : Exception()


    private fun toJSON(): String {

        val fullRequest = GSON.toJson(requestMarket, requestMarket.javaClass)

        Log.e("Test", fullRequest)

        return fullRequest
    }

    data class SocketUpdate(
        val text: String? = null,
        val byteString: ByteString? = null,
        val exception: Throwable? = null
    )

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}