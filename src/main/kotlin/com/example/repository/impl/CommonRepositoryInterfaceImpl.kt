package com.example.repository.impl

import com.example.repository.CommonRepositoryInterface
import lombok.AllArgsConstructor
import org.hibernate.Session
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Repository
@AllArgsConstructor
class CommonRepositoryInterfaceImpl<T>(
    private val entityManager: EntityManager
) : CommonRepositoryInterface<T> {

    @Transactional
    override fun <S : T?> persist(entity: S): S {
        entityManager.persist(entity)
        return entity
    }

    @Transactional
    override fun <S : T?> merge(entity: S): S {
        return entityManager.merge(entity)
    }

    @Transactional
    override fun <S : T?> update(entity: S) {
        session().update(entity)
    }

    protected fun session(): Session {
        return entityManager.unwrap(Session::class.java)
    }

}
