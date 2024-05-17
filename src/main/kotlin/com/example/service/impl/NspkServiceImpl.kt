package com.example.service.impl

import com.example.entity.Request
import com.example.model.NspkRequest
import com.example.model.NspkResponse
import com.example.service.NspkService
import org.springframework.stereotype.Service
import java.util.*

@Service
class NspkServiceImpl: NspkService {

    override fun sendOperation(request: NspkRequest, operationRequest: Request): NspkResponse {
        // here must be NSPK client call that is now mocked
        val success = (0..1).random() == 1
        return NspkResponse(
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
    }

}