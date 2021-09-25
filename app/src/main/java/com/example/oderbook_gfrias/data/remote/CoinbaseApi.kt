package com.example.oderbook_gfrias.data.remote

import com.example.oderbook_gfrias.data.remote.dto.CoinDto

interface CoinbaseApi {

    suspend fun getData(): List<CoinDto>
}