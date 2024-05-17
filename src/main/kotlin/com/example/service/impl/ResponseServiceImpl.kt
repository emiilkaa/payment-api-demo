package com.example.service.impl

import com.example.entity.Request
import com.example.enums.RequestStatus
import com.example.model.NspkResponse
import com.example.model.OperationResponse
import com.example.service.ResponseService
import org.springframework.stereotype.Service

@Service
class ResponseServiceImpl: ResponseService {

    override fun createResponse(nspkResponse: NspkResponse, request: Request) = OperationResponse(
        success = nspkResponse.success,
        status = if (nspkResponse.success) RequestStatus.SUCCESS else RequestStatus.FAILED,
        responseCode = nspkResponse.responseCode,
        requestId = request.id,
        message = if (nspkResponse.success) "" else (nspkResponse.errorMessage ?: "UNKNOWN"),
        extension = request.extensionFields
    )

    override fun createBadResponse(errorMessage: String) = OperationResponse(
        success = false,
        status = RequestStatus.FAILED,
        responseCode = "-100",
        requestId = null,
        message = errorMessage,
        extension = mutableMapOf()
    )

}