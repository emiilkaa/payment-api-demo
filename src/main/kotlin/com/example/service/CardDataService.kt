package com.example.service

import com.example.entity.CardData
import com.example.model.CardInfo
import java.time.LocalDateTime

interface CardDataService {

    fun createCardData(cardInfo: CardInfo, dttm: LocalDateTime): CardData
    fun saveCardData(cardData: CardData): CardData

}