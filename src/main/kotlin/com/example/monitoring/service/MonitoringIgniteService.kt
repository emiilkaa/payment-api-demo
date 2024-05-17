package com.example.monitoring.service

import org.aspectj.lang.ProceedingJoinPoint

interface MonitoringIgniteService {

    fun monitoringCache(joinPoint: ProceedingJoinPoint, actionName: String, cacheName: String): Any?

}