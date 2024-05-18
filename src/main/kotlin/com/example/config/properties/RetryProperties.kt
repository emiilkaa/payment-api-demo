package com.example.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("app.retry")
data class RetryProperties(
    val retryCount: Int,
    val delay: Int
)
