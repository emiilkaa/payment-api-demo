package com.example.service

import com.example.entity.CardData
import com.example.model.CardInfo

interface CardDataService {

    fun createCardData(cardInfo: CardInfo): CardData
    fun saveCardData(cardData: CardData): CardData

}