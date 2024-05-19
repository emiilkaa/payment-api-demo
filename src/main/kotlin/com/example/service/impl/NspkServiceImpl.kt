package com.example.service.impl

import com.example.entity.NspkData
import com.example.entity.Request
import com.example.entity.extension.getId
import com.example.model.NspkRequest
import com.example.model.NspkResponse
import com.example.repository.NspkDataRepository
import com.example.service.NspkService
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import java.util.*

@Service
class NspkServiceImpl(
    val nspkDataRepository: NspkDataRepository,
    val objectMapper: ObjectMapper
): NspkService {

    override fun sendOperation(request: NspkRequest, operationRequest: Request): NspkResponse {
        // here must be NSPK client call that is now mocked
        val success = (0..1).random() == 1
        val response = NspkResponse(
            requestId = request.requestId,
            messageType = request.messageType,
            cardPan = request.cardPan,
            expDate = request.expDate,
            terminalId = request.terminalId,
            amount = request.amount,
            internalId = UUID.randomUUID().toString(),
            success = success,
            responseCode = if (success) "0" else "1",
            errorCode = if (success) null else "-192",
            errorMessage = if (success) null else "Not approved"
        )
        saveNspkResponse(request, response, operationRequest)
        return response
    }

    private fun saveNspkResponse(nspkRequest: NspkRequest, nspkResponse: NspkResponse, request: Request) {
        val nspkData = NspkData(
            dateCreated = request.dateCreated,
            responseCode = nspkResponse.responseCode,
            errorCode = nspkResponse.errorCode,
            errorMessage = nspkResponse.errorMessage,
            requestMessage = objectMapper.convertValue(nspkRequest, object:
                TypeReference<Map<String, Any>>() {}),
            responseMessage = objectMapper.convertValue(nspkResponse, object:
                TypeReference<Map<String, Any>>() {}),
            requestId = request.getId(),
            paymentId = request.payment.getId()
        )
        nspkDataRepository.save(nspkData)
    }

}