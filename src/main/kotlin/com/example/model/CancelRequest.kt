package com.example.model

import com.example.enums.RequestType
import com.example.model.extension.defaultRequest
import java.math.BigDecimal
import java.time.LocalDateTime

data class CancelRequest(
    val originalRequestId: Long,
    val amount: BigDecimal,
    val terminalId: String,
    val extension: MutableMap<String, String>,
    val requestType: RequestType,
    val operationDate: LocalDateTime
) : OperationRequest() {

    override fun request() = defaultRequest()

    override fun requestType() = requestType

    override fun additionalInfo(): MutableMap<String, String> = extension
}