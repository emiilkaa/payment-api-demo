package com.example.service.impl

import com.example.common.LOGGER
import com.example.enums.RequestStatus
import com.example.model.OperationRequest
import com.example.model.OperationResponse
import com.example.service.OperationService
import com.example.service.operation.Operation
import org.springframework.stereotype.Service
import java.lang.Exception

@Service
class OperationServiceImpl(
    val operations: List<Operation<OperationRequest>>
) : OperationService {

    override fun handleOperation(request: OperationRequest): OperationResponse {
        return try {
            val operation = getOperation(request)
            val operationData = operation.getOperationData(request)
            operation.handleRequest(operationData)
        } catch (ex: Exception) {
            return OperationResponse(
                success = false,
                status = RequestStatus.FAILED,
                responseCode = "1",
                requestId = null,
                message = ex.message ?: "UNKNOWN",
                extension = request.additionalInfo()
            )
        }
    }

    private fun getOperation(request: OperationRequest): Operation<OperationRequest> {
        LOGGER.OPERATION.info("Get operation request with type {}", request.requestType())
        return operations.firstOrNull {
            it.getType() == request.requestType()
        } ?: throw RuntimeException("Operation not implemented")
    }

}