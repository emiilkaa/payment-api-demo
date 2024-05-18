package com.example.model

data class OperationInfo(
    val paymentInfo: PaymentInfo,
    val cardDataInfo: CardDataInfo,
    val requests: List<RequestInfo>
)
