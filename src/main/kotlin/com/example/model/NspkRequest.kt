package com.example.model

import java.math.BigDecimal

data class NspkRequest(
    val requestId: Long,
    val messageType: String,
    val cardPan: String?,
    val expDate: String?,
    val terminalId: String,
    val amount: BigDecimal
)
