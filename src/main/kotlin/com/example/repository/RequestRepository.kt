package com.example.repository

import com.example.entity.Request
import org.springframework.data.jpa.repository.JpaRepository

interface RequestRepository: JpaRepository<Request, Long>,
    PersistRepository<Request>, UpdateRepository, HQLRequestRepository