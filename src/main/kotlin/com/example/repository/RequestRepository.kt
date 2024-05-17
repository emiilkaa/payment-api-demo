package com.example.repository

import com.example.entity.Request
import org.springframework.data.jpa.repository.JpaRepository

interface RequestRepository: JpaRepository<Request, Long>, CommonRepositoryInterface<Request> {

    fun findByIdOrThrow(requestId: Long): Request {
        return findById(requestId)
            .orElseThrow { throw RuntimeException("Request not found by id $requestId") }
    }

}