package com.example.service.impl

import com.example.entity.Payment
import com.example.entity.extension.cardDataInfo
import com.example.entity.extension.paymentInfo
import com.example.entity.extension.requestInfo
import com.example.model.OperationInfo
import com.example.monitoring.MonitoringConstants
import com.example.monitoring.annotation.MonitoringOperationInfo
import com.example.service.OperationInfoService
import com.example.service.RequestService
import org.springframework.stereotype.Service

@Service
class OperationInfoServiceImpl(
    private val requestService: RequestService
) : OperationInfoService {

    @MonitoringOperationInfo(MonitoringConstants.BY_REQUEST_ID)
    override fun getOperationInfoByRequestId(requestId: Long): OperationInfo {
        val request = requestService.getRequest(requestId)
        return operationInfoByPayment(request.payment)
    }

    @MonitoringOperationInfo(MonitoringConstants.BY_PAYMENT_ID)
    override fun getOperationInfoByPaymentId(paymentId: Long): OperationInfo {
        val requests = requestService.getRequestsByPaymentId(paymentId)
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
