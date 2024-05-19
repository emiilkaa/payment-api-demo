package com.example.repository.impl

import com.example.repository.PersistRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Repository
class PersistRepositoryImpl<T>(val entityManager: EntityManager) : PersistRepository<T> {

    @Transactional
    override fun <S : T> persist(entity: S): S {
        entityManager.persist(entity)
        return entity
    }

}