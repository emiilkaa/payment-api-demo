package com.example.repository

import com.example.entity.Payment
import com.example.entity.Request

interface UpdateRepository {

    fun update(payment: Payment)
    fun update(request: Request)

}