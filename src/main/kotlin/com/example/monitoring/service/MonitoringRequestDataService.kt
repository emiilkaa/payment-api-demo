package com.example.monitoring.service

import com.example.model.OperationRequest
import org.aspectj.lang.ProceedingJoinPoint

interface MonitoringRequestDataService {

    fun monitoringRequestEnrichment(joinPoint: ProceedingJoinPoint, operationRequest: OperationRequest): Any?

}