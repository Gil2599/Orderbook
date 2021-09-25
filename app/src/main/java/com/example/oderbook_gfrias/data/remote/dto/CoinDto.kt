package com.example.oderbook_gfrias.data.remote.dto

import com.example.oderbook_gfrias.data.model.Coin

data class CoinDto(
    val changes: List<List<String>>,
    val product_id: String,
    val time: String,
    val type: String
)

fun CoinDto.toCoin(): Coin {
    return Coin(
        changes = changes,
        product_id = product_id,
        type = type
    )
}