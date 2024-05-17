package com.example.model

import java.math.BigDecimal

data class NspkResponse(
    val requestId: Long,
    val messageType: String,
    val cardPan: String?,
    val expDate: String?,
    val terminalId: String,
    val amount: BigDecimal,
    val internalId: String,
    val success: Boolean,
    val responseCode: String,
    val errorCode: String?,
    val errorMessage: String?
)
