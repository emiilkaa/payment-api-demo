package com.example.service.impl

import com.example.common.LOGGER
import com.example.entity.Payment
import com.example.entity.Request
import com.example.enums.PaymentStatus
import com.example.enums.RequestStatus
import com.example.enums.RequestType
import com.example.model.OperationResponse
import com.example.repository.RequestRepository
import com.example.service.PaymentService
import com.example.service.RequestService
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class RequestServiceImpl(
    private val paymentService: PaymentService,
    private val requestRepository: RequestRepository
): RequestService {

    override fun getRequest(requestId: Long): Request {
        return requestRepository.findByRequestId(requestId)
    }

    override fun linkRequest(requestId: Long, request: Request, payment: Payment): Request {
        val originalRequest = payment.requests.firstOrNull { it.id == requestId }
            ?: throw RuntimeException("Request with id $requestId not found")
        request.payment = originalRequest.payment
        request.terminalId = originalRequest.terminalId
        return request
    }

    override fun saveRequest(request: Request): Request {
        try {
            return requestRepository.save(request)
        } catch (ex: Exception) {
            LOGGER.REQUEST.info("Unable to save request info ${ex.message}", ex)
            throw ex
        }
    }

    override fun finishRequest(result: OperationResponse, request: Request) {
        try {
            saveResponseRequest(request, result)
            request.payment.status = definePaymentStatus(request)
            paymentService.updatePayment(request.payment)
        } catch (ex: Exception) {
            LOGGER.REQUEST.error("Unable to update request status ${ex.message}", ex)
        }
    }

    override fun failRequest(request: Request) {
        try {
            takeIf { request.id != null }?.let {
                request.status = RequestStatus.FAILED
                request.message = "FAIL"
                requestRepository.update(request)
                request.payment.status = definePaymentStatus(request)
                paymentService.updatePayment(request.payment)
            }
        } catch (ex: Exception) {
            LOGGER.REQUEST.error("Unable to failed request", ex)
        }
    }

    private fun definePaymentStatus(request: Request): PaymentStatus {
        val paymentStatus = request.payment.status
        val requestType = request.requestType
        val success = request.status == RequestStatus.SUCCESS
        return when (requestType) {
            RequestType.PAY -> if (success) PaymentStatus.PAID else PaymentStatus.FAILED
            RequestType.CANCEL -> if (success) defineCancelStatus(request.payment.amount) else paymentStatus
        }
    }

    private fun saveResponseRequest(request: Request, result: OperationResponse) {
        request.status = if (result.success) RequestStatus.SUCCESS else RequestStatus.FAILED
        request.message = result.message
        requestRepository.update(request)
    }

    private fun defineCancelStatus(amount: BigDecimal): PaymentStatus {
        return if (amount > BigDecimal.ZERO)
            PaymentStatus.PARTIAL_REFUNDED
        else PaymentStatus.REFUNDED
    }

}