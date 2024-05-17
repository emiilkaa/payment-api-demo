package com.example.monitoring.service

import com.example.model.OperationData
import com.example.model.OperationRequest
import org.aspectj.lang.ProceedingJoinPoint

interface MonitoringOperationService {

    fun monitoringOperation(joinPoint: ProceedingJoinPoint, message: OperationData<OperationRequest>): Any?

}