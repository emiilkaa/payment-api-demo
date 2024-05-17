package com.example.service

import com.example.entity.Request
import com.example.model.NspkResponse
import com.example.model.OperationResponse

interface ResponseService {

    fun createResponse(nspkResponse: NspkResponse, request: Request): OperationResponse
    fun createBadResponse(errorMessage: String): OperationResponse

}