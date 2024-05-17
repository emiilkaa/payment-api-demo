package com.example.service

import com.example.entity.Payment

interface PaymentService {

    fun savePayment(payment: Payment): Payment
    fun updatePayment(payment: Payment)

}