package com.example.repository

import com.example.entity.CardData
import org.springframework.data.jpa.repository.JpaRepository

interface CardDataRepository: JpaRepository<CardData, Long>, CommonRepositoryInterface<CardData> {
}