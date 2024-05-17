package com.example.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app.ignite")
data class IgniteProperties(
    val addresses: String,
    val expirePolicyDuration: Long
)