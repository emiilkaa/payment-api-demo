package com.example.repository

import com.example.entity.NspkData
import org.springframework.data.jpa.repository.JpaRepository

interface NspkDataRepository: JpaRepository<NspkData, Long>, PersistRepository<NspkData>