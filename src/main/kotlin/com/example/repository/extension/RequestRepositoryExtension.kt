package com.example.repository.extension

import com.example.entity.Request
import com.example.repository.RequestRepository

fun RequestRepository.findByIdOrThrow(requestId: Long): Request {
    return findById(requestId)
        .orElseThrow { throw RuntimeException("Request not found by id $requestId") }
}