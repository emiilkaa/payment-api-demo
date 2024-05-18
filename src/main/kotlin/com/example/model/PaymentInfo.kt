package com.example.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class PaymentInfo(
    val paymentDate: LocalDateTime,
    val paymentId: Long,
    val status: String,
    val amount: BigDecimal,
    val originalAmount: BigDecimal
)
