package com.example.model.extension

import com.example.entity.Request
import com.example.enums.RequestStatus
import com.example.enums.RequestType
import com.example.model.CancelRequest
import com.example.model.PayRequest

fun PayRequest.defaultRequest() = Request(
    requestType = RequestType.PAY,
    amount = amount,
    status = RequestStatus.IN_PROCESS,
    terminalId = terminalId,
    message = "OK",
    extensionFields = extension
)

fun CancelRequest.defaultRequest() = Request(
    requestType = RequestType.CANCEL,
    amount = amount,
    status = RequestStatus.IN_PROCESS,
    terminalId = terminalId,
    message = "OK",
    extensionFields = extension
)