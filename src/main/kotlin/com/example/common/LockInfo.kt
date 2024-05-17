package com.example.common

import java.time.LocalDateTime

data class LockInfo(
    val id: String,
    val date: LocalDateTime = LocalDateTime.now(),
    val cacheName: String,
    val locked: Boolean = true
)
