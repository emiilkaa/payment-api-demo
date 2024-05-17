package com.example.repository

import com.example.entity.Payment
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository: JpaRepository<Payment, Long>, CommonRepositoryInterface<Payment> {
}