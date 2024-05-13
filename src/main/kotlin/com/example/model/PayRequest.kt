package com.example.model

import com.example.enums.RequestType
import java.math.BigDecimal
import java.time.LocalDateTime

data class PayRequest(
    val amount: BigDecimal,
    val terminalId: String,
    val operationDate: LocalDateTime,
    val cardInfo: CardInfo,
    val extension: MutableMap<String, String>,
    val requestType: RequestType
) : OperationRequest() {

    override fun requestType() = requestType

    override fun additionalInfo(): MutableMap<String, String> = extension

}