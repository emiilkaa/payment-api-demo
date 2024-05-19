package com.example.repository

import com.example.entity.CardData
import com.example.entity.NspkData
import com.example.entity.Payment
import com.example.entity.Request
import com.example.enums.PaymentScheme
import com.example.enums.PaymentStatus
import com.example.enums.RequestStatus
import com.example.enums.RequestType
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import java.sql.ResultSet

@Component
class RequestRowMapper(
    val objectMapper: ObjectMapper
) : RowMapper<Request> {

    override fun mapRow(rs: ResultSet, rowNum: Int): Request {
        val request = mapRequest(rs)
            ?: throw RuntimeException("Request not found")

        mapPayment(rs)?.let {
            request.payment = it
            request.payment.requestsSet().add(request)
        }

        mapNspkData(rs)?.let { request.nspkData = it }

        mapCardData(rs)?.let {
            request.payment.cardData = it
            it.payment = request.payment
        }

        return request
    }

    private fun mapRequest(rs: ResultSet): Request? {
        val requestId = rs.getString("r_id")?.toLong()
        return requestId?.let {
            Request(
                id = requestId,
                dateCreated = rs.getTimestamp("r_date_created").toLocalDateTime(),
                dateUpdated = rs.getTimestamp("r_date_updated").toLocalDateTime(),
                requestType = rs.getString("r_request_type").let {
                    RequestType.valueOf(it)
                },
                amount = rs.getBigDecimal("r_amount"),
                status = rs.getString("r_status").let {
                    RequestStatus.valueOf(it)
                },
                terminalId = rs.getString("r_terminal_id"),
                message = rs.getString("r_message"),
                extensionFields = rs.getString("r_extension_fields")?.let {
                    objectMapper.readValue(it, MAP_STRING_STRING).toMutableMap()
                } ?: mutableMapOf()
            )
        }
    }

    private fun mapPayment(rs: ResultSet): Payment? {
        val paymentId: Long? = rs.getString("p_id")?.toLong()
        return paymentId?.let {
            Payment(
                id = paymentId,
                dateCreated = rs.getTimestamp("p_date_created").toLocalDateTime(),
                dateUpdated = rs.getTimestamp("p_date_updated").toLocalDateTime(),
                amount = rs.getBigDecimal("p_amount"),
                originalAmount = rs.getBigDecimal("p_original_amount"),
                status = rs.getString("p_status").let {
                    PaymentStatus.valueOf(it)
                },
                additionalData = rs.getString("p_additional_data").let {
                    objectMapper.readValue(it, MAP_STRING_ANY).toMutableMap()
                }
            )
        }
    }

    private fun mapNspkData(rs: ResultSet): NspkData? {
        val nspkDataId: Long? = rs.getString("nd_id")?.toLong()
        return nspkDataId?.let {
            NspkData(
                id = nspkDataId,
                dateCreated = rs.getTimestamp("nd_date_created").toLocalDateTime(),
                dateUpdated = rs.getTimestamp("nd_date_updated").toLocalDateTime(),
                responseCode = rs.getString("nd_response_code"),
                errorCode = rs.getString("nd_error_code"),
                errorMessage = rs.getString("nd_error_message"),
                requestMessage = rs.getString("nd_request_msg")?.let {
                    objectMapper.readValue(it, MAP_STRING_ANY).toMutableMap()
                },
                responseMessage = rs.getString("nd_response_msg")?.let {
                    objectMapper.readValue(it, MAP_STRING_ANY).toMutableMap()
                },
                paymentId = rs.getLong("nd_payment_id"),
                requestId = rs.getLong("nd_request_id")
            )
        }
    }

    private fun mapCardData(rs: ResultSet): CardData? {
        val cardDataId: Long? = rs.getString("cd_id")?.toLong()
        return cardDataId?.let {
            CardData(
                id = cardDataId,
                dateCreated = rs.getTimestamp("cd_date_created").toLocalDateTime(),
                dateUpdated = rs.getTimestamp("cd_date_updated").toLocalDateTime(),
                paymentScheme = rs.getString("cd_payment_scheme").let {
                    PaymentScheme.valueOf(it)
                },
                pan = rs.getString("cd_pan"),
                panExpDate = rs.getString("cd_pan_exp_date"),
                cardholderName = rs.getString("cd_cardholder_name")
            )
        }
    }

    companion object {
        val MAP_STRING_STRING = object : TypeReference<Map<String, String>>() {}
        val MAP_STRING_ANY = object : TypeReference<Map<String, Any>>() {}
    }

}