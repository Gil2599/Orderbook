package com.example.oderbook_gfrias.data.model.repository

import com.example.oderbook_gfrias.data.remote.CoinbaseWebSocketListener
import com.example.oderbook_gfrias.data.remote.WebServicesProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel

class MainRepository constructor(private val webServicesProvider: WebServicesProvider) {

    @ExperimentalCoroutinesApi
    fun startSocket(): Channel<CoinbaseWebSocketListener.SocketUpdate> =
        webServicesProvider.startSocket()

    @ExperimentalCoroutinesApi
    fun closeSocket() {
        webServicesProvider.stopSocket()
    }
}