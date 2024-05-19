package com.example.repository.impl

import com.example.entity.Payment
import com.example.entity.Request
import com.example.entity.extension.getId
import com.example.enums.RequestType
import com.example.repository.HQLRequestRepository
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Repository
class HQLRequestRepositoryImpl(
    var postgresJdbcTemplate: NamedParameterJdbcTemplate,
    val requestRowMapper: RowMapper<Request>
) : HQLRequestRepository {

    @Transactional(readOnly = true)
    override fun findByRequestId(requestId: Long): Request {
        val currentDate = currentDate()
        val parameters = mutableMapOf<String, Any>(
            Pair(REQUEST_ID_PARAM, requestId),
            Pair(CURRENT_DATE_PARAM, FORMATTER.format(currentDate))
        )

        return postgresJdbcTemplate.query(
            SELECT_REQUEST_BY_REQUEST_ID,
            parameters,
            requestRowMapper
        ).apply {
            if (this.size != 1) {
                throw RuntimeException("Request not found by id $requestId")
            }
        }.first()
    }

    @Transactional(readOnly = true)
    override fun findByRequestIdAndDateCreatedAfter(requestId: Long, dateCreated: LocalDateTime): Request {
        val currentDate = currentDate()
        val parameters = mutableMapOf<String, Any>(
            Pair(REQUEST_ID_PARAM, requestId),
            Pair(DATE_CREATED_PARAM, FORMATTER.format(dateCreated)),
            Pair(CURRENT_DATE_PARAM, FORMATTER.format(currentDate))
        )

        return postgresJdbcTemplate.query(
            SELECT_REQUEST_BY_REQUEST_ID_AND_DATE_CREATED_AFTER,
            parameters,
            requestRowMapper
        ).apply {
            if (this.size != 1) {
                throw RuntimeException("Request not found by id $requestId and dateCreated: $dateCreated")
            }
        }.first()
    }

    override fun findByRequestIdAndDateCreatedAfterAndDateCreatedBefore(requestId: Long, startDate: LocalDateTime, endDate: LocalDateTime): Request {
        val parameters = mutableMapOf<String, Any>(
            Pair(REQUEST_ID_PARAM, requestId),
            Pair(DATE_CREATED_PARAM, FORMATTER.format(startDate)),
            Pair(CURRENT_DATE_PARAM, FORMATTER.format(endDate))
        )

        return postgresJdbcTemplate.query(
            SELECT_REQUEST_BY_REQUEST_ID_AND_DATE_CREATED_AFTER,
            parameters,
            requestRowMapper
        ).apply {
            if (this.size != 1) {
                throw RuntimeException("Request not found by id $requestId and startDate: $startDate and endDate: $endDate")
            }
        }.first()
    }

    @Transactional(readOnly = true)
    override fun findByPaymentId(paymentId: Long, date: LocalDateTime): List<Request> {
        val currentDate = currentDate()
        val parameters = mutableMapOf<String, Any>(
            Pair(PAYMENT_ID_PARAM, paymentId),
            Pair(DATE_CREATED_PARAM, FORMATTER.format(date)),
            Pair(CURRENT_DATE_PARAM, FORMATTER.format(currentDate))
        )

        return postgresJdbcTemplate.query(
            SELECT_REQUESTS_BY_PAYMENT_ID,
            parameters,
            requestRowMapper
        )
    }

    @Transactional(readOnly = true)
    override fun findByPaymentAndRequestType(payment: Payment, type: RequestType, date: LocalDateTime): List<Request> {
        val currentDate = currentDate()
        val parameters = mutableMapOf<String, Any>(
            Pair(PAYMENT_ID_PARAM, payment.getId()),
            Pair(REQUEST_TYPE_PARAM, type.name),
            Pair(DATE_CREATED_PARAM, FORMATTER.format(date)),
            Pair(CURRENT_DATE_PARAM, FORMATTER.format(currentDate))
        )

        return postgresJdbcTemplate.query(
            SELECT_REQUEST_BY_PAYMENT_ID_AND_TYPE,
            parameters,
            requestRowMapper
        )
    }

    private fun currentDate(): LocalDateTime? = LocalDateTime.now().plusHours(1)

    companion object {
        private val FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        private const val REQUEST_ID_PARAM = "requestId"
        private const val PAYMENT_ID_PARAM = "paymentId"
        private const val REQUEST_TYPE_PARAM = "requestType"
        private const val DATE_CREATED_PARAM = "dateCreated"
        private const val CURRENT_DATE_PARAM = "currentDate"

        private val REQUEST_FULL = """
            r.id r_id,
            r.date_created r_date_created,
            r.date_updated r_date_updated,
            r.request_type r_request_type,
            r.amount r_amount,
            r.status r_status,
            r.terminal_id r_terminal_id,
            r.message r_message,
            r.extension_fields r_extension_fields
        """.trim()
        private val NSPK_DATA_FULL = """
            nd.id nd_id,
            nd.date_created nd_date_created,
            nd.date_updated nd_date_updated,
            nd.response_code nd_response_code,
            nd.error_code nd_error_code,
            nd.error_message nd_error_message,
            nd.request_message nd_request_msg,
            nd.response_message nd_response_msg,
            nd.payment_id nd_payment_id,
            nd.request_id nd_request_id
        """.trim()
        private val PAYMENT_FULL = """
            p.id p_id,
            p.date_created p_date_created,
            p.date_updated p_date_updated,
            p.amount p_amount,
            p.original_amount p_original_amount,
            p.status p_status,
            p.additional_data p_additional_data
        """.trim()
        private val CARD_DATA_FULL = """
            cd.id cd_id,
            cd.date_created cd_date_created,
            cd.date_updated cd_date_updated,
            cd.payment_scheme cd_payment_scheme,
            cd.pan cd_pan,
            cd.pan_exp_date cd_pan_exp_date,
            cd.cardholder_name cd_cardholder_name
        """.trim()

        private val SELECT_REQUEST_BY_REQUEST_ID = """
            select
              $REQUEST_FULL,
              $NSPK_DATA_FULL,
              $PAYMENT_FULL,
              $CARD_DATA_FULL
            from payment_api_app.request r
              left join payment_api_app.nspk_data nd on nd.request_id = r.id
                and nd.date_created <= :$CURRENT_DATE_PARAM::timestamp
              join payment_api_app.payment p on p.id = r.payment_id
                and p.date_created <= :$CURRENT_DATE_PARAM::timestamp
              join payment_api_app.card_data cd on cd.payment_id = p.id
                and cd.date_created <= :$CURRENT_DATE_PARAM::timestamp
            where r.id = :$REQUEST_ID_PARAM
              and r.date_created <= :$CURRENT_DATE_PARAM::timestamp
        """.trimIndent()

        private val SELECT_REQUEST_BY_PAYMENT_ID_AND_TYPE = """
            select
              $REQUEST_FULL,
              $NSPK_DATA_FULL,
              $PAYMENT_FULL,
              $CARD_DATA_FULL
            from payment_api_app.request r
              left join payment_api_app.nspk_data nd on nd.request_id = r.id
                and nd.date_created >= :$DATE_CREATED_PARAM::timestamp
                and nd.date_created <= :$CURRENT_DATE_PARAM::timestamp
              join payment_api_app.payment p on p.id = r.payment_id
                and p.date_created >= :$DATE_CREATED_PARAM::timestamp
                and p.date_created <= :$CURRENT_DATE_PARAM::timestamp
              join payment_api_app.card_data cd on cd.payment_id = p.id
                and cd.date_created >= :$DATE_CREATED_PARAM::timestamp
                and cd.date_created <= :$CURRENT_DATE_PARAM::timestamp
            where r.payment_id = :$PAYMENT_ID_PARAM
              and r.request_type = :$REQUEST_TYPE_PARAM
              and r.date_created >= :$DATE_CREATED_PARAM::timestamp
              and r.date_created <= :$CURRENT_DATE_PARAM::timestamp
            """.trimIndent()

        private val SELECT_REQUESTS_BY_PAYMENT_ID = """
            select
              $REQUEST_FULL,
              $NSPK_DATA_FULL,
              $PAYMENT_FULL,
              $CARD_DATA_FULL
            from payment_api_app.request r
              left join payment_api_app.nspk_data nd on nd.request_id = r.id
                and nd.date_created >= :$DATE_CREATED_PARAM::timestamp
                and nd.date_created <= :$CURRENT_DATE_PARAM::timestamp
              join payment_api_app.payment p on p.id = r.payment_id
                and p.date_created >= :$DATE_CREATED_PARAM::timestamp
                and p.date_created <= :$CURRENT_DATE_PARAM::timestamp
              join payment_api_app.card_data cd on cd.payment_id = p.id
                and cd.date_created >= :$DATE_CREATED_PARAM::timestamp
                and cd.date_created <= :$CURRENT_DATE_PARAM::timestamp
            where r.payment_id = :$PAYMENT_ID_PARAM
              and r.date_created >= :$DATE_CREATED_PARAM::timestamp
              and r.date_created <= :$CURRENT_DATE_PARAM::timestamp
            """.trimIndent()

        private val SELECT_REQUEST_BY_REQUEST_ID_AND_DATE_CREATED_AFTER = """
            with request_payment as materialized (
              select 
                $REQUEST_FULL,
                $PAYMENT_FULL
              from payment_api_app.request as r
                inner join payment_api_app.payment p on p.id = r.payment_id
                  and p.date_created >= :$DATE_CREATED_PARAM::timestamp
                  and p.date_created <= :$CURRENT_DATE_PARAM::timestamp
              where r.id = :$REQUEST_ID_PARAM
                and r.date_created >= :$DATE_CREATED_PARAM::timestamp
                and r.date_created <= :$CURRENT_DATE_PARAM::timestamp
            )
            select 
              rp.*,
              $NSPK_DATA_FULL,
              $CARD_DATA_FULL
            from request_payment as rp
              inner join payment_api_app.card_data as cd on cd.payment_id = rp.p_id
                and cd.date_created >= :$DATE_CREATED_PARAM::timestamp
                and cd.date_created <= :$CURRENT_DATE_PARAM::timestamp
              left join payment_api_app.nspk_data as nd on nd.request_id = rp.r_id
                and nd.date_created >= :$DATE_CREATED_PARAM::timestamp
                and nd.date_created <= :$CURRENT_DATE_PARAM::timestamp
            """.trimIndent()
    }
}