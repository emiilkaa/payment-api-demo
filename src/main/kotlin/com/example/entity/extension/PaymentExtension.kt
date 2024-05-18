package com.example.entity.extension

import com.example.entity.Payment
import com.example.model.PaymentInfo

fun Payment.getId() = id ?: throw RuntimeException("paymentId is missing")

fun Payment.paymentInfo() = PaymentInfo(
    paymentDate = dateCreated,
    paymentId = getId(),
    status = status.name,
    amount = amount,
    originalAmount = originalAmount
)