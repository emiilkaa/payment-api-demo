package com.example.service.operation

import com.example.enums.RequestType
import com.example.model.OperationRequest
import com.example.model.OperationResponse

interface Operation<Req : OperationRequest> {

    fun handleRequest(request: Req): OperationResponse
    fun getType(): RequestType

}