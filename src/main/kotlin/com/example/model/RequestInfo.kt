package com.example.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class RequestInfo(
    val operationDate: LocalDateTime,
    val requestId: Long,
    val requestType: String,
    val terminalId: String,
    val amount: BigDecimal,
    val status: String,
    val extensionFields: Map<String, String>
)
