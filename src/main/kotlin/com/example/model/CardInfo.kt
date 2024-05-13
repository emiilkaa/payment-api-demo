package com.example.model

data class CardInfo(
    val cardNumber: String,
    val expDate: String,
    val cardHolderName: String? = null
)