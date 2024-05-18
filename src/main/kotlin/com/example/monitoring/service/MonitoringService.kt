package com.example.monitoring.service

import com.example.common.LockInfo
import com.example.model.OperationData
import com.example.model.OperationRequest
import org.aspectj.lang.ProceedingJoinPoint

interface MonitoringService {

    fun monitoringOperation(joinPoint: ProceedingJoinPoint, operationData: OperationData<OperationRequest>): Any?
    fun monitoringMessage(joinPoint: ProceedingJoinPoint, request: OperationRequest): Any?
    fun monitoringLock(joinPoint: ProceedingJoinPoint, id: String, cacheName: String): Any?
    fun monitoringLock(joinPoint: ProceedingJoinPoint, lockInfo: LockInfo): Any?
    fun monitoringIgnite(joinPoint: ProceedingJoinPoint, id: String, cacheName: String): Any?
    fun monitoringIgnite(joinPoint: ProceedingJoinPoint, lockInfo: LockInfo): Any?
    fun monitoringOperationInfo(joinPoint: ProceedingJoinPoint): Any?

}