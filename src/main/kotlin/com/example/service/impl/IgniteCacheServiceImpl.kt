package com.example.service.impl

import com.example.common.LOGGER
import com.example.common.LockInfo
import com.example.model.exception.NeedRetryException
import com.example.monitoring.annotation.MonitoringIgnite
import com.example.service.IgniteCacheService
import org.apache.ignite.client.ClientCache
import org.apache.ignite.client.ClientCacheConfiguration
import org.apache.ignite.client.IgniteClient
import org.springframework.retry.RetryCallback
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class IgniteCacheServiceImpl(
    val igniteClient: IgniteClient,
    val lockRetryTemplate: RetryTemplate,
    val caches: List<ClientCacheConfiguration>
): IgniteCacheService {

    @MonitoringIgnite
    override fun lock(id: String, cacheName: String): LockInfo {
        return try {
            val cache = getCache(cacheName)
            val date = lockRetryTemplate.execute(RetryCallback<LocalDateTime, Exception> {
                tryLock(cache, id)
            })
            LockInfo(id, date, cacheName, true)
        } catch (ex: Exception) {
            LOGGER.LOCK.error("Error obtain lock for id $id", ex)
            LockInfo(id = id, cacheName = cacheName, locked = false)
        }
    }

    @MonitoringIgnite
    override fun unlock(lockInfo: LockInfo) {
        try {
            val cache = getCache(lockInfo.cacheName)
            cache.remove(lockInfo.id)
        } catch (ex: Exception) {
            LOGGER.LOCK.error("Error remove lock key", ex)
        }
    }

    private fun tryLock(cache: ClientCache<String, LocalDateTime>, id: String): LocalDateTime {
        val now = LocalDateTime.now()
        if (!cache.putIfAbsent(id, now)) {
            LOGGER.LOCK.warn("Lock already exists for id $id")
            throw NeedRetryException("Lock already exist for id: $id")
        }
        return now
    }

    private fun getCache(cacheName: String) = caches.firstOrNull { it.name == cacheName }
        ?.let { igniteClient.getOrCreateCache<String, LocalDateTime>(it) }
        ?: throw RuntimeException("Cache for lock with name $cacheName not found")

}
