package com.example.controller

import com.example.model.OperationInfo
import com.example.service.OperationInfoService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/operation-info")
class OperationInfoController(
    private val operationInfoService: OperationInfoService
) {

    @GetMapping(INFO_BY_PAYMENT)
    fun getOperationInfoByPaymentId(@PathVariable paymentId: Long): OperationInfo {
        return operationInfoService.getOperationInfoByPaymentId(paymentId)
    }

    @GetMapping(INFO_BY_REQUEST)
    fun getOperationInfoByRequestId(@PathVariable requestId: Long): OperationInfo {
        return operationInfoService.getOperationInfoByRequestId(requestId)
    }

    companion object {
        const val INFO_BY_PAYMENT: String = "/payment/{paymentId}"
        const val INFO_BY_REQUEST: String = "/request/{requestId}"
    }

}