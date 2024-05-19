package com.example.service.operation

import com.example.entity.Payment
import com.example.entity.Request
import com.example.entity.extension.getId
import com.example.enums.RequestType
import com.example.model.*
import com.example.service.*
import org.springframework.stereotype.Service

@Service
class CancelOperation(
    lockService: LockService,
    val requestService: RequestService,
    val responseService: ResponseService,
    val nspkService: NspkService
) : AbstractOperation<CancelRequest>(
    requestService,
    lockService
) {

    override fun getPaymentInfo(request: CancelRequest): Payment {
        return requestService.getRequest(request.originalRequestId).payment
    }

    override fun getRequestInfo(request: CancelRequest, payment: Payment): Request {
        return requestService.linkRequest(request.originalRequestId, request.request(), payment)
    }

    override fun getType(): RequestType = RequestType.CANCEL

    override fun execute(operationData: OperationData<CancelRequest>): OperationResponse {
        val payment = operationData.request.payment
        if (payment.amount < operationData.request.amount) {
            throw RuntimeException("Amount is greater than available")
        }
        val nspkRequest = createCancelMessage(operationData.request)
        val response = nspkService.sendOperation(nspkRequest, operationData.request)
        if (response.success) {
            updatePaymentAmount(operationData.request)
        }
        return responseService.createResponse(response, operationData.request)
    }

    private fun updatePaymentAmount(request: Request) {
        val payment = request.payment
        payment.amount = payment.amount.minus(request.amount)
    }

    private fun createCancelMessage(request: Request) = NspkRequest(
        requestId = request.getId(),
        messageType = "CA",
        cardPan = request.payment.cardData.pan,
        expDate = request.payment.cardData.panExpDate,
        terminalId = request.terminalId,
        amount = request.amount
    )
}