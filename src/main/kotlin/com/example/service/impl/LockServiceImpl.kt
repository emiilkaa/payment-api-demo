package com.example.service.impl

import com.example.common.LOGGER
import com.example.common.LockInfo
import com.example.config.properties.RetryProperties
import com.example.monitoring.MonitoringConstants
import com.example.monitoring.annotation.MonitoringLock
import com.example.service.IgniteCacheService
import com.example.service.LockService
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

@Service
class LockServiceImpl(
    val igniteCacheService: IgniteCacheService,
    val lockExecutor: Executor,
    val retryProperties: RetryProperties
) : LockService {

    @MonitoringLock(action = MonitoringConstants.LOCK_ACTION)
    override fun lock(id: String, cacheName: String): LockInfo {
        val lockInfoFuture = CompletableFuture.supplyAsync(
            { igniteCacheService.lock(id, cacheName) },
            lockExecutor
        )

        val lockTimeout = calculateLockTimeout()
        return try {
            lockInfoFuture.get(lockTimeout, TimeUnit.MILLISECONDS)
        } catch (ex: Exception) {
            LOGGER.LOCK.error("Timeout obtaining lock for id $id")
            LockInfo(id = id, cacheName = cacheName, locked = false)
        }
    }

    @MonitoringLock(action = MonitoringConstants.UNLOCK_ACTION)
    override fun unlock(lockInfo: LockInfo) {
        if (!lockInfo.locked)
            return

        val unlockFuture = CompletableFuture.runAsync(
            { igniteCacheService.unlock(lockInfo) },
            lockExecutor
        )

        val unlockTimeout = calculateUnlockTimeout()
        try {
            unlockFuture.get(unlockTimeout, TimeUnit.MILLISECONDS)
        } catch (ex: Exception) {
            LOGGER.LOCK.error("Timeout unlocking for id ${lockInfo.id}")
        }
    }

    private fun calculateLockTimeout() = retryProperties.retryCount * retryProperties.delay.toLong()

    private fun calculateUnlockTimeout() = retryProperties.delay.toLong()

}
