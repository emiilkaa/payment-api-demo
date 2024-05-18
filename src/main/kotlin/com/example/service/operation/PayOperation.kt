package com.example.service.operation

import com.example.common.LOGGER
import com.example.entity.Payment
import com.example.entity.Request
import com.example.entity.extension.getId
import com.example.enums.PaymentStatus
import com.example.enums.RequestType
import com.example.model.*
import com.example.service.*
import org.springframework.stereotype.Service

@Service
class PayOperation(
    requestService: RequestService,
    lockService: LockService,
    val paymentService: PaymentService,
    val nspkService: NspkService,
    val cardDataService: CardDataService,
    val responseService: ResponseService
) : AbstractOperation<PayRequest>(
    requestService,
    lockService
) {

    override fun getPaymentInfo(request: PayRequest): Payment {
        val payment = getPayment(request)
        val cardData = cardDataService.createCardData(request.cardInfo, request.operationDate)
        cardData.payment = payment
        payment.cardData = cardData
        return payment
    }

    override fun getRequestInfo(request: PayRequest, payment: Payment): Request {
        val result = request.request()
        result.payment = payment
        return result
    }

    override fun savePayment(payment: Payment) {
        paymentService.savePayment(payment)
        payment.cardData = cardDataService.saveCardData(payment.cardData)
    }

    override fun getType() = RequestType.PAY

    override fun execute(operationData: OperationData<PayRequest>): OperationResponse {
        LOGGER.OPERATION.info("Start pay operation for request ${operationData.request.id}")
        val nspkRequest = createPayMessage(operationData.request, operationData.operationRequest.cardInfo)
        val response = nspkService.sendOperation(nspkRequest, operationData.request)
        return responseService.createResponse(response, operationData.request)
    }

    private fun getPayment(request: PayRequest) = Payment(
        dateCreated = request.operationDate,
        amount = request.amount,
        originalAmount = request.amount,
        status = PaymentStatus.IN_PROCESS,
        additionalData = request.additionalInfo()
    )

    private fun createPayMessage(request: Request, cardInfo: CardInfo) = NspkRequest(
        requestId = request.getId(),
        messageType = "PA",
        cardPan = cardInfo.cardNumber,
        expDate = cardInfo.expDate,
        terminalId = request.terminalId,
        amount = request.amount
    )

}