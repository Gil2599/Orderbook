package com.example.oderbook_gfrias.data.remote

import android.util.Log
import com.example.oderbook_gfrias.data.remote.dto.CoinbaseRequest
import okio.ByteString
import com.google.gson.GsonBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.*

@ExperimentalCoroutinesApi
class CoinbaseWebSocketListener: WebSocketListener() {

    val socketEventChannel: Channel<SocketUpdate> = Channel()

    private val GSON = GsonBuilder().setPrettyPrinting().create()

    override fun onOpen(webSocket: WebSocket, response: Response) {
        //Log.e("Message", toJSON())
        webSocket.send(toJSON())
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.e("Message", text)
        GlobalScope.launch {
            socketEventChannel.send(SocketUpdate(text))
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        GlobalScope.launch {
            socketEventChannel.send(SocketUpdate(exception = SocketAbortedException()))
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

        val request = CoinbaseRequest(
            listOf("level2"),
            listOf("BTC-USDC", "ETH_BTC"),
            "subscribe"
        )

        val fullRequest = GSON.toJson(request, request.javaClass)

        Log.e("Test", fullRequest)

        return fullRequest
    }

    /*fun start() {
        val request: Request = Request.Builder().url("wss://ws-feed.pro.coinbase.com").build()
        val listener = CoinbaseWebSocketListener()

        val wb: WebSocket = client.newWebSocket(request, listener)

        client.dispatcher().executorService().shutdown()

    }*/

    data class SocketUpdate(
        val text: String? = null,
        val byteString: ByteString? = null,
        val exception: Throwable? = null
    )

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}