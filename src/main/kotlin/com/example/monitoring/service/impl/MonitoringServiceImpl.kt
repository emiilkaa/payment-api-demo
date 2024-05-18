package com.example.monitoring.service.impl

import com.example.common.LockInfo
import com.example.model.OperationData
import com.example.model.OperationRequest
import com.example.monitoring.MonitoringConstants
import com.example.monitoring.annotation.MonitoringLock
import com.example.monitoring.annotation.MonitoringOperationInfo
import com.example.monitoring.service.*
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class MonitoringServiceImpl(
    val monitoringIgniteService: MonitoringIgniteService,
    val monitoringLockService: MonitoringLockService,
    val monitoringOperationService: MonitoringOperationService,
    val monitoringRequestDataService: MonitoringRequestDataService,
    val monitoringOperationInfoService: MonitoringOperationInfoService
) : MonitoringService {

    @Around(
        "@annotation(com.example.monitoring.annotation.MonitoringOperation) && args(operationData)",
        argNames = "operationData"
    )
    override fun monitoringOperation(joinPoint: ProceedingJoinPoint, operationData: OperationData<OperationRequest>): Any? {
        return monitoringOperationService.monitoringOperation(joinPoint, operationData)
    }

    @Around(
        "@annotation(com.example.monitoring.annotation.MonitoringRequestData) && args(request)",
        argNames = "request"
    )
    override fun monitoringMessage(joinPoint: ProceedingJoinPoint, request: OperationRequest): Any? {
        return monitoringRequestDataService.monitoringRequestEnrichment(joinPoint, request)
    }

    @Around("@annotation(com.example.monitoring.annotation.MonitoringLock) && args(id,cacheName)", argNames = "id,cacheName")
    override fun monitoringLock(joinPoint: ProceedingJoinPoint, id: String, cacheName: String): Any? {
        val annotation: MonitoringLock = getAnnotation(joinPoint, MonitoringLock::class.java)
        val action = annotation.action
        return monitoringLockService.monitoringLock(joinPoint, action, cacheName)
    }

    @Around("@annotation(com.example.monitoring.annotation.MonitoringLock) && args(lockInfo)", argNames = "lockInfo")
    override fun monitoringLock(joinPoint: ProceedingJoinPoint, lockInfo: LockInfo): Any? {
        val annotation: MonitoringLock = getAnnotation(joinPoint, MonitoringLock::class.java)
        val action = annotation.action
        return monitoringLockService.monitoringLock(joinPoint, action, lockInfo.cacheName)
    }

    @Around("@annotation(com.example.monitoring.annotation.MonitoringIgnite) && args(id,cacheName)", argNames = "id,cacheName")
    override fun monitoringIgnite(joinPoint: ProceedingJoinPoint, id: String, cacheName: String): Any? {
        return monitoringIgniteService.monitoringCache(joinPoint, MonitoringConstants.LOCK_ACTION, cacheName)
    }

    @Around("@annotation(com.example.monitoring.annotation.MonitoringIgnite) && args(lockInfo)", argNames = "lockInfo")
    override fun monitoringIgnite(joinPoint: ProceedingJoinPoint, lockInfo: LockInfo): Any? {
        return monitoringIgniteService.monitoringCache(joinPoint, MonitoringConstants.UNLOCK_ACTION, lockInfo.cacheName)
    }

    @Around("@annotation(com.example.monitoring.annotation.MonitoringOperationInfo)")
    override fun monitoringOperationInfo(joinPoint: ProceedingJoinPoint, id: Long): Any? {
        val annotation: MonitoringOperationInfo = getAnnotation(joinPoint, MonitoringOperationInfo::class.java)
        val requestType = annotation.requestType
        return monitoringOperationInfoService.monitoringOperationInfo(joinPoint, requestType)
    }

    private fun <T : Annotation> getAnnotation(joinPoint: ProceedingJoinPoint, annotationClass: Class<T>): T {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        return method.getAnnotation(annotationClass)
    }

}