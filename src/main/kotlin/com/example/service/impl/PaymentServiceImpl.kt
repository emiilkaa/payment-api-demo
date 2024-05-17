package com.example.service.impl

import com.example.common.LOGGER
import com.example.entity.Payment
import com.example.repository.PaymentRepository
import com.example.service.PaymentService
import org.springframework.stereotype.Service

@Service
class PaymentServiceImpl(
    val paymentRepository: PaymentRepository
) : PaymentService {

    override fun savePayment(payment: Payment): Payment {
        return try {
            paymentRepository.save(payment)
        } catch (ex: Exception) {
            LOGGER.REQUEST.error("Unable to save pay ${ex.message.toString()}", ex)
            throw ex
        }
    }

    override fun updatePayment(payment: Payment) {
        try {
            paymentRepository.update(payment)
        } catch (ex: Exception) {
            LOGGER.REQUEST.error("Unable to update payment ${ex.message.toString()}", ex)
        }
    }

}
