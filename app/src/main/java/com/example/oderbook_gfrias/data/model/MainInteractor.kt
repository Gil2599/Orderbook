package com.example.oderbook_gfrias.data.model

import com.example.oderbook_gfrias.data.model.repository.MainRepository
import com.example.oderbook_gfrias.data.remote.CoinbaseWebSocketListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel

class MainInteractor constructor(private val repository: MainRepository) {

    @ExperimentalCoroutinesApi
    fun stopSocket() {
        repository.closeSocket()
    }

    @ExperimentalCoroutinesApi
    fun startSocket(): Channel<CoinbaseWebSocketListener.SocketUpdate> = repository.startSocket()

}