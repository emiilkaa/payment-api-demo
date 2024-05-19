package com.example.repository

import com.example.entity.Payment
import com.example.entity.Request
import com.example.enums.RequestType
import java.time.LocalDateTime

interface HQLRequestRepository {

    fun findByRequestId(requestId: Long): Request
    fun findByRequestIdAndDateCreatedAfter(requestId: Long, dateCreated: LocalDateTime): Request
    fun findByRequestIdAndDateCreatedAfterAndDateCreatedBefore(requestId: Long, startDate: LocalDateTime, endDate: LocalDateTime): Request
    fun findByPaymentId(paymentId: Long, date: LocalDateTime): List<Request>
    fun findByPaymentAndRequestType(payment: Payment, type: RequestType, date: LocalDateTime): List<Request>

}