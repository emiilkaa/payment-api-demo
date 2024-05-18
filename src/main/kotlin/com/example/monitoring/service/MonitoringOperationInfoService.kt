package com.example.monitoring.service

import org.aspectj.lang.ProceedingJoinPoint

interface MonitoringOperationInfoService {

    fun monitoringOperationInfo(joinPoint: ProceedingJoinPoint, requestType: String): Any?

}