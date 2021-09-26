package com.example.oderbook_gfrias.common

import com.example.oderbook_gfrias.data.model.CoinbaseRequest

object Constants {

    val BTC_USD = CoinbaseRequest(
        listOf("level2"),
        listOf("BTC-USDC"),
        "subscribe"
    )

    val ETH_BTC = CoinbaseRequest(
        listOf("level2"),
        listOf("ETH-BTC"),
        "subscribe"
    )

    val BTC_USD_MARKET = Pair("BTC", "USDC")

    val ETH_BTC_MARKET = Pair("ETH", "BTC")
}