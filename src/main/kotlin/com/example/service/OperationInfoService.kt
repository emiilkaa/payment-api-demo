package com.example.service

import com.example.model.OperationInfo
import java.time.LocalDateTime

interface OperationInfoService {

    fun getOperationInfoByRequestId(requestId: Long): OperationInfo
    fun getOperationInfoByPaymentId(paymentId: Long, operationDate: LocalDateTime): OperationInfo

}