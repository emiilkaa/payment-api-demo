package com.example.service.impl

import com.example.entity.Payment
import com.example.entity.extension.cardDataInfo
import com.example.entity.extension.paymentInfo
import com.example.entity.extension.requestInfo
import com.example.model.OperationInfo
import com.example.monitoring.MonitoringConstants
import com.example.monitoring.annotation.MonitoringOperationInfo
import com.example.repository.RequestRepository
import com.example.service.OperationInfoService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class OperationInfoServiceImpl(
    private val requestRepository: RequestRepository
) : OperationInfoService {

    @MonitoringOperationInfo(MonitoringConstants.BY_REQUEST_ID)
    override fun getOperationInfoByRequestId(requestId: Long): OperationInfo {
        val request = requestRepository.findByRequestId(requestId)
        return operationInfoByPayment(request.payment)
    }

    @MonitoringOperationInfo(MonitoringConstants.BY_PAYMENT_ID)
    override fun getOperationInfoByPaymentId(paymentId: Long, operationDate: LocalDateTime): OperationInfo {
        val requests = requestRepository.findByPaymentId(paymentId, operationDate)
        return operationInfoByPayment(requests.first().payment)
    }

    private fun operationInfoByPayment(payment: Payment): OperationInfo {
        val paymentInfo = payment.paymentInfo()
        val cardDataInfo = payment.cardData.cardDataInfo()
        val requests = payment.requestsSet()
            .map { request -> request.requestInfo() }
            .sortedBy { it.operationDate }

        return OperationInfo(
            paymentInfo = paymentInfo,
            cardDataInfo = cardDataInfo,
            requests = requests
        )
    }

}
