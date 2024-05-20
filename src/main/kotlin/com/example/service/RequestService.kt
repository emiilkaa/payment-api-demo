package com.example.service

import com.example.entity.Payment
import com.example.entity.Request
import com.example.model.OperationResponse

interface RequestService {

    fun getRequest(requestId: Long): Request
    fun getRequestsByPaymentId(paymentId: Long): List<Request>
    fun linkRequest(requestId: Long, request: Request, payment: Payment): Request
    fun saveRequest(request: Request): Request
    fun finishRequest(result: OperationResponse, request: Request)
    fun failRequest(request: Request)

}