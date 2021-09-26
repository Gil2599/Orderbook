package com.example.oderbook_gfrias.data.remote.dto

data class MarketUpdateDto(
    val changes: List<List<String>>,
    val product_id: String,
    val time: String,
    val type: String
)

