package com.example.service.operation

import com.example.enums.RequestType
import com.example.model.OperationData
import com.example.model.OperationRequest
import com.example.model.OperationResponse

interface Operation<Req : OperationRequest> {

    fun handleRequest(operationData: OperationData<Req>): OperationResponse
    fun getOperationData(request: Req): OperationData<Req>
    fun getType(): RequestType

}