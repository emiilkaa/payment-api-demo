package com.example.service

import com.example.common.LockInfo

interface LockService {

    fun lock(id: String, cacheName: String): LockInfo
    fun unlock(lockInfo: LockInfo)

}