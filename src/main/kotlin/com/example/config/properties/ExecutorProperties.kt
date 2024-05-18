package com.example.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("app.executor")
data class ExecutorProperties(
    val coreSize: Int,
    val maxSize: Int
)
