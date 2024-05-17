package com.example.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app.executor")
data class ExecutorProperties(
    val coreSize: Int,
    val maxSize: Int
)
