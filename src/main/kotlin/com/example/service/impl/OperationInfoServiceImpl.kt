package com.example.service.impl

import com.example.entity.Payment
import com.example.entity.extension.cardDataInfo
import com.example.entity.extension.paymentInfo
import com.example.entity.extension.requestInfo
import com.example.model.OperationInfo
import com.example.monitoring.MonitoringConstants
import com.example.monitoring.annotation.MonitoringOperationInfo
import com.example.repository.PaymentRepository
import com.example.repository.RequestRepository
import com.example.repository.extension.findByIdOrThrow
import com.example.service.OperationInfoService
import org.springframework.stereotype.Service

@Service
class OperationInfoServiceImpl(
    private val paymentRepository: PaymentRepository,
    private val requestRepository: RequestRepository
) : OperationInfoService {

    @MonitoringOperationInfo(MonitoringConstants.BY_REQUEST_ID)
    override fun getOperationInfoByRequestId(requestId: Long): OperationInfo {
        val request = requestRepository.findByIdOrThrow(requestId)
        val payment = request.payment
        return operationInfoByPayment(payment)
    }

    @MonitoringOperationInfo(MonitoringConstants.BY_PAYMENT_ID)
    override fun getOperationInfoByPaymentId(paymentId: Long): OperationInfo {
        val payment = paymentRepository.findById(paymentId)
            .orElseThrow { IllegalArgumentException("Payment with id $paymentId not found") }
        return operationInfoByPayment(payment)
    }

    private fun operationInfoByPayment(payment: Payment): OperationInfo {
        val paymentInfo = payment.paymentInfo()
        val cardDataInfo = payment.cardData.cardDataInfo()
        val requests = payment.requests.map { request -> request.requestInfo() }

        return OperationInfo(
            paymentInfo = paymentInfo,
            cardDataInfo = cardDataInfo,
            requests = requests
        )
    }

}
