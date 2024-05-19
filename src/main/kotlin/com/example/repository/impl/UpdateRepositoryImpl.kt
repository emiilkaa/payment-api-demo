package com.example.repository.impl

import com.example.common.LOGGER
import com.example.common.QueryName
import com.example.entity.Payment
import com.example.entity.Request
import com.example.entity.extension.fillQueryParameters
import com.example.entity.extension.getId
import com.example.entity.extension.updateQuery
import com.example.repository.UpdateRepository
import org.hibernate.Session
import org.hibernate.query.Query
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import javax.persistence.EntityManager

@Component
class UpdateRepositoryImpl(val entityManager: EntityManager) : UpdateRepository {

    @Transactional
    override fun update(payment: Payment) {
        payment.dateUpdated = LocalDateTime.now()
        val query = session().createQuery(payment.updateQuery() + WHERE_CONDITION)
        payment.fillQueryParameters(query)
        fillWhereAndExecute(payment.getId(), payment.dateCreated, query)
    }

    @Transactional
    override fun update(request: Request) {
        request.dateUpdated = LocalDateTime.now()
        val query = session().createQuery(request.updateQuery() + WHERE_CONDITION)
        request.fillQueryParameters(query)
        fillWhereAndExecute(request.getId(), request.dateCreated, query)
    }

    private fun <R> fillWhereAndExecute(id: Long, dateCreated: LocalDateTime, query: Query<R>): Int {
        val count = query.setParameter(QueryName.PARAMETER_DATE_FROM, dateCreated.minusMinutes(DEFAULT_BETWEEN_MINUTES))
            .setParameter(QueryName.PARAMETER_DATE_TO, dateCreated.plusMinutes(DEFAULT_BETWEEN_MINUTES))
            .setParameter(QueryName.PARAMETER_ID, id)
            .executeUpdate()
        checkUpdateCount(count, query.queryString)
        return count
    }


    private fun checkUpdateCount(count: Int, queryString: String) {
        if (count > 1) {
            LOGGER.REPOSITORY_UPDATE.error(
                "Incorrect update count for query $queryString (must be 1, but found $count). Rollback..."
            )
            throw RuntimeException("Incorrect update count")
        }
    }

    protected fun session(): Session {
        return entityManager.unwrap(Session::class.java)
    }

    companion object {
        const val DEFAULT_BETWEEN_MINUTES = 10L

        const val WHERE_CONDITION = """
               where id = :${QueryName.PARAMETER_ID}
                 and dateCreated > to_timestamp(:${QueryName.PARAMETER_DATE_FROM}, 'YYYY-MM-DD HH24:MI:SS')
                 and dateCreated < to_timestamp(:${QueryName.PARAMETER_DATE_TO}, 'YYYY-MM-DD HH24:MI:SS')
        """
    }
}