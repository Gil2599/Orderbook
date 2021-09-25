package com.example.oderbook_gfrias.data.remote

import android.util.Log
import android.widget.Toast
import com.example.oderbook_gfrias.data.remote.dto.CoinbaseRequest
import com.google.gson.Gson
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import com.google.gson.GsonBuilder;

class CoinbaseWebSocket: WebSocketListener() {

    private val GSON = GsonBuilder().setPrettyPrinting().create()

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.e("Message", toJSON())
        webSocket.send(toJSON())
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.e("Message", text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("Test", t.toString())
    }

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
}