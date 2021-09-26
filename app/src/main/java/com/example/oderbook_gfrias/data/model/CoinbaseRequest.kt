package com.example.oderbook_gfrias.data.model

data class CoinbaseRequest(
    val channels: List<Any>,
    val product_ids: List<String>,
    val type: String
)