package com.example.oderbook_gfrias.data.remote

import com.example.oderbook_gfrias.data.model.CoinbaseRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class WebServicesProvider(marketRequest: CoinbaseRequest) {

    private var _webSocket: WebSocket? = null

    private val client = OkHttpClient()
    private val request: Request = Request.Builder().url("wss://ws-feed.pro.coinbase.com").build()

    @ExperimentalCoroutinesApi
    private var _webSocketListener = CoinbaseWebSocketListener(marketRequest)

    @ExperimentalCoroutinesApi
    fun startSocket(): MutableSharedFlow<CoinbaseWebSocketListener.SocketUpdate> =

        with(_webSocketListener) {

            startSocket(this)
            this@with.socketEventChannel
        }

    @ExperimentalCoroutinesApi
    fun startSocket(webSocketListener: CoinbaseWebSocketListener) {
        _webSocketListener = webSocketListener

        _webSocket = client.newWebSocket(request, webSocketListener)
        client.dispatcher.executorService.shutdown()
    }

    @ExperimentalCoroutinesApi
    fun stopSocket() {
        try {
            _webSocket?.close(NORMAL_CLOSURE_STATUS, null)
            _webSocket = null
            //_webSocketListener.socketEventChannel.close()
        } catch (ex: Exception) {
        }
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }

}