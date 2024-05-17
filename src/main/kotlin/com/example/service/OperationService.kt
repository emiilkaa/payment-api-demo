package com.example.service

import com.example.model.OperationRequest
import com.example.model.OperationResponse

interface OperationService {

    fun handleOperation(request: OperationRequest): OperationResponse

}