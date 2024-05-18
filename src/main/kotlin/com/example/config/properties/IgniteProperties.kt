package com.example.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("app.ignite")
data class IgniteProperties(
    val addresses: String,
    val expirePolicyDuration: Long
)