package com.example.entity.extension

import com.example.entity.CardData
import com.example.model.CardDataInfo

fun CardData.getMaskedCardNumber(): String? {
    return pan?.let {
        val firstFour = it.take(4)
        val lastFour = it.takeLast(4)
        val maskedMiddle = "*".repeat(it.length - 8)
        firstFour + maskedMiddle + lastFour
    }
}

fun CardData.cardDataInfo() = CardDataInfo(
    maskedCardNumber = getMaskedCardNumber(),
    paymentScheme = paymentScheme.name
)