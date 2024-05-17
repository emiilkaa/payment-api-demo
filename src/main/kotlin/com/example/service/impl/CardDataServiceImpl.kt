package com.example.service.impl

import com.example.entity.CardData
import com.example.enums.PaymentScheme
import com.example.model.CardInfo
import com.example.repository.CardDataRepository
import com.example.service.CardDataService
import org.springframework.stereotype.Service

@Service
class CardDataServiceImpl(
    val cardDataRepository: CardDataRepository
) : CardDataService {

    override fun createCardData(cardInfo: CardInfo): CardData {
        return CardData(
            pan = cardInfo.cardNumber,
            panExpDate = cardInfo.expDate,
            cardholderName = cardInfo.cardHolderName,
            paymentScheme = PaymentScheme.values()[(0..3).random()]
        )
    }

    override fun saveCardData(cardData: CardData): CardData {
        return cardDataRepository.save(cardData)
    }

}