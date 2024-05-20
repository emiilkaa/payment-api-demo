package com.example.repository

import com.example.entity.Request
import java.time.LocalDateTime

interface HQLRequestRepository {

    fun findByRequestId(requestId: Long): Request
    fun findByRequestIdAndDateCreatedAfter(requestId: Long, dateCreated: LocalDateTime): Request
    fun findByPaymentId(paymentId: Long): List<Request>

}