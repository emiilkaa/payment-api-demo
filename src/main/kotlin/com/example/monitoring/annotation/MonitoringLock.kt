package com.example.monitoring.annotation

@Target(AnnotationTarget.FUNCTION)
annotation class MonitoringLock(
    val action: String
)
