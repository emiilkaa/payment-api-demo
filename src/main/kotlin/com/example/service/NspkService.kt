package com.example.service

import com.example.entity.Request
import com.example.model.NspkRequest
import com.example.model.NspkResponse

interface NspkService {

    fun sendOperation(request: NspkRequest, operationRequest: Request): NspkResponse

}