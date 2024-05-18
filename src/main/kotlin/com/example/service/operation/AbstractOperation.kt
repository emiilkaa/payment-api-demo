package com.example.service.operation

import com.example.common.LOGGER
import com.example.common.LockName
import com.example.entity.Payment
import com.example.entity.Request
import com.example.model.OperationData
import com.example.model.OperationRequest
import com.example.model.OperationResponse
import com.example.monitoring.annotation.MonitoringOperation
import com.example.monitoring.annotation.MonitoringRequestData
import com.example.service.LockService
import com.example.service.RequestService

abstract class AbstractOperation<Req : OperationRequest>(
    private val requestService: RequestService,
    private val lockService: LockService
) : Operation<Req> {

    @MonitoringOperation
    override fun handleRequest(operationData: OperationData<Req>): OperationResponse {
        LOGGER.OPERATION.info("Operation {} called for request", getType())
        val terminalId = operationData.request.terminalId
        val lock = lockService.lock(terminalId, LockName.TERMINAL_ID_LOCK_NAME)
        lateinit var result: OperationResponse
        try {
            saveRequest(operationData)
            result = execute(operationData)
            requestService.finishRequest(result, operationData.request)
            return result
        } catch (ex: Exception) {
            LOGGER.OPERATION.error("Exception while executing operation: ${ex.message}", ex)
            requestService.failRequest(operationData.request)
            throw ex
        } finally {
            lockService.unlock(lock)
        }
    }

    @MonitoringRequestData
    override fun getOperationData(request: Req): OperationData<Req> {
        LOGGER.OPERATION.info("Getting information for request {}", getType())
        val payment = getPaymentInfo(request)
        return OperationData(
            request = getRequestInfo(request, payment),
            operationRequest = request
        )
    }

    abstract fun getPaymentInfo(request: Req): Payment

    abstract fun getRequestInfo(request: Req, payment: Payment): Request

    private fun saveRequest(operationData: OperationData<Req>) {
        savePayment(operationData.request.payment)
        operationData.request.id = requestService.saveRequest(operationData.request).id
    }

    open fun savePayment(payment: Payment) {
        LOGGER.OPERATION.info("Saving payment info")
    }

    abstract fun execute(operationData: OperationData<Req>): OperationResponse

}