package com.example.model

import com.example.entity.Request

data class OperationData<Req : OperationRequest>(
    val request: Request,
    val operationRequest: Req
)
