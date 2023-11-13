package com.example.fraud.model

data class Customer(
    val accountNumber: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val debitCardNumber: String,
    val gender: String,
    val email: String,
    val phone: String,
    val address: String,
    val city: String,
    val state: String,
    val zip: String,
    val country: String,
    val occupation: String,
    val balance: Double
)
