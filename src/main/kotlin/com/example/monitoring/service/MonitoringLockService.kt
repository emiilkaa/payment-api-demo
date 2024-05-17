package com.example.monitoring.service

import org.aspectj.lang.ProceedingJoinPoint

interface MonitoringLockService {

    fun monitoringLock(joinPoint: ProceedingJoinPoint, action: String, cacheName: String): Any?

}