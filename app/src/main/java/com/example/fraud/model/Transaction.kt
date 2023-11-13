package com.example.fraud.model

import java.io.Serializable

data class Transaction(
    val transactionID: String,
    val zip: Long,
    val gender: Long,
    val city: Long,
    val state: Long,
    val category: Long,
    val amt: Double,
    val merchant: String,
    val lat: Double,
    val long: Double,
    val merch_lat: Double,
    val merch_long: Double,
    val city_pop: Long,
    val unix_time: Long,
    val transTime: String
) : Serializable
