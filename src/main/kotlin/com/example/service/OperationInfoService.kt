package com.example.service

import com.example.model.OperationInfo

interface OperationInfoService {

    fun getOperationInfoByRequestId(requestId: Long): OperationInfo
    fun getOperationInfoByPaymentId(paymentId: Long): OperationInfo

}