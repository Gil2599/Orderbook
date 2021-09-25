package com.example.oderbook_gfrias.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.oderbook_gfrias.data.remote.CoinbaseWebSocket
import com.example.oderbook_gfrias.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val client: OkHttpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        start()

    }

    private fun start() {
        val request: Request = Request.Builder().url("wss://ws-feed.pro.coinbase.com").build()
        val listener = CoinbaseWebSocket()

        val wb: WebSocket = client.newWebSocket(request, listener)

        client.dispatcher().executorService().shutdown()

    }
}