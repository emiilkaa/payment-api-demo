package com.example.model

import com.example.enums.RequestStatus

data class OperationResponse(
    val success: Boolean = true,
    val status: RequestStatus,
    val responseCode: String,
    val requestId: Long?,
    val message: String,
    val extension: MutableMap<String, String>
)