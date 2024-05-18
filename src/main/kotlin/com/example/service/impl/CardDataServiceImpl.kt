package com.example.service.impl

import com.example.entity.CardData
import com.example.enums.PaymentScheme
import com.example.model.CardInfo
import com.example.repository.CardDataRepository
import com.example.service.CardDataService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CardDataServiceImpl(
    val cardDataRepository: CardDataRepository
) : CardDataService {

    override fun createCardData(cardInfo: CardInfo, dttm: LocalDateTime): CardData {
        return CardData(
            dateCreated = dttm,
            pan = cardInfo.cardNumber,
            panExpDate = cardInfo.expDate,
            cardholderName = cardInfo.cardHolderName,
            paymentScheme = PaymentScheme.values()[(0..2).random()]
        )
    }

    override fun saveCardData(cardData: CardData): CardData {
        return cardDataRepository.save(cardData)
    }

    override fun getCardDataByPayment(paymentId: Long): CardData {
        return cardDataRepository.findByPaymentId(paymentId)
            ?: throw RuntimeException("CardData not found for payment $paymentId")
    }

}