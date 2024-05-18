package com.example.model

data class HttpResponse(
    val success: Boolean,
    val code: Long,
    val message: String,
    val details: String?
)
