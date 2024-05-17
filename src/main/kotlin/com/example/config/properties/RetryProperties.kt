package com.example.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app.retry")
data class RetryProperties(
    val retryCount: Int,
    val delay: Int
)
