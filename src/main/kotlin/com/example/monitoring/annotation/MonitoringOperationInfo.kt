package com.example.monitoring.annotation

@Target(AnnotationTarget.FUNCTION)
annotation class MonitoringOperationInfo(
    val requestType: String
)
