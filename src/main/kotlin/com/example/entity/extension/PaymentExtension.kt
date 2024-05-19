package com.example.entity.extension

import com.example.entity.Payment_
import com.example.entity.Payment
import com.example.model.PaymentInfo
import org.hibernate.query.Query
import javax.persistence.TemporalType

fun Payment.getId() = id ?: throw RuntimeException("paymentId is missing")

fun Payment.paymentInfo() = PaymentInfo(
    paymentDate = dateCreated,
    paymentId = getId(),
    status = status.name,
    amount = amount,
    originalAmount = originalAmount
)

fun Payment.updateQuery(): String = UPDATE_PAYMENT

fun <R> Payment.fillQueryParameters(query: Query<R>) {
    query.setParameter(Payment_.DATE_UPDATED, dateUpdated, TemporalType.TIMESTAMP)
        .setParameter(Payment_.AMOUNT, amount)
        .setParameter(Payment_.ORIGINAL_AMOUNT, originalAmount)
        .setParameter(Payment_.ADDITIONAL_DATA, additionalData)
        .setParameter(Payment_.STATUS, status)
}

const val UPDATE_PAYMENT = """
             update Payment
                set ${Payment_.DATE_UPDATED} = :${Payment_.DATE_UPDATED},
                    ${Payment_.AMOUNT} = :${Payment_.AMOUNT},
                    ${Payment_.ORIGINAL_AMOUNT} = :${Payment_.ORIGINAL_AMOUNT},
                    ${Payment_.ADDITIONAL_DATA} = :${Payment_.ADDITIONAL_DATA},
                    ${Payment_.STATUS} = :${Payment_.STATUS}
        """