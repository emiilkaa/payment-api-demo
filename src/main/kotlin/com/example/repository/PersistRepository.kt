package com.example.repository

interface PersistRepository<T> {

    fun <S : T> persist(entity: S): S

}