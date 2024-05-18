package com.example.entity.extension

import com.example.entity.Request
import com.example.model.RequestInfo

fun Request.getId() = id ?: throw RuntimeException("requestId is missing")

fun Request.requestInfo() = RequestInfo(
    requestId = getId(),
    operationDate = dateCreated,
    requestType = requestType.name,
    amount = amount,
    status = status.name,
    terminalId = terminalId,
    extensionFields = extensionFields
)