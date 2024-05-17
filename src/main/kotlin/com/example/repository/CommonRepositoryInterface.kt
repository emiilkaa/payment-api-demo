package com.example.repository

interface CommonRepositoryInterface<T> {

    fun <S : T?> persist(entity: S): S
    fun <S : T?> merge(entity: S): S
    fun <S : T?> update(entity: S)

}