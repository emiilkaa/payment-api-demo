package com.example.entity.extension

import com.example.entity.Request
import com.example.entity.Request_
import com.example.model.RequestInfo
import org.hibernate.query.Query
import javax.persistence.TemporalType

fun Request.getId() = id ?: throw RuntimeException("requestId is missing")

fun Request.requestInfo() = RequestInfo(
    requestId = getId(),
    operationDate = dateCreated,
    requestType = requestType.name,
    amount = amount,
    status = status.name,
    terminalId = terminalId,
    extensionFields = extensionFields
)

fun Request.updateQuery(): String = UPDATE_REQUEST

fun <R> Request.fillQueryParameters(query: Query<R>) {
    query.setParameter(Request_.AMOUNT, amount)
        .setParameter(Request_.REQUEST_TYPE, requestType)
        .setParameter(Request_.TERMINAL_ID, terminalId)
        .setParameter(Request_.MESSAGE, message)
        .setParameter(Request_.DATE_UPDATED, dateUpdated, TemporalType.TIMESTAMP)
        .setParameter(Request_.EXTENSION_FIELDS, extensionFields)
        .setParameter(Request_.STATUS, status)
}

const val UPDATE_REQUEST = """
            update Request
                set ${Request_.AMOUNT} = :${Request_.AMOUNT},
                    ${Request_.REQUEST_TYPE} = :${Request_.REQUEST_TYPE},
                    ${Request_.TERMINAL_ID} = :${Request_.TERMINAL_ID},
                    ${Request_.MESSAGE} = :${Request_.MESSAGE},
                    ${Request_.DATE_UPDATED} = :${Request_.DATE_UPDATED},
                    ${Request_.EXTENSION_FIELDS} = :${Request_.EXTENSION_FIELDS},
                    ${Request_.STATUS} = :${Request_.STATUS}
        """