package com.example.monitoring.service.impl

import com.example.common.LOGGER
import com.example.common.LockInfo
import com.example.monitoring.MonitoringConstants
import com.example.monitoring.service.MonitoringLockService
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Timer
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class MonitoringLockServiceImpl(
    val meterRegistry: MeterRegistry
) : MonitoringLockService {

    override fun monitoringLock(joinPoint: ProceedingJoinPoint, action: String, cacheName: String): Any? {
        val start = meterRegistry.config().clock().wallTime()
        try {
            val result = joinPoint.proceed()
            if (result is LockInfo) {
                monitoring(start, action, cacheName, result.locked)
            } else {
                monitoring(start, action, cacheName, true)
            }
            return result
        } catch (e: Throwable) {
            LOGGER.MONITORING.error("Unexpected lock monitoring exception. Message: {}", e.message)
            monitoring(start, action, cacheName, false)
            throw e
        }
    }

    private fun monitoring(start: Long, actionName: String, cacheName: String, success: Boolean) {
        try {
            val defaultTags = defaultTags(actionName, cacheName)
            val additionalTags = additionalTags(success)
            val allTags = defaultTags + additionalTags
            measureLockCount(allTags.toMutableSet())
            measureLockTime(defaultTags, start)
        } catch (ex: Exception) {
            LOGGER.MONITORING.error("Unable to monitor lock", ex)
        }
    }

    private fun measureLockCount(tags: MutableSet<Tag>) {
        Counter.builder(MonitoringConstants.METRIC_LOCK_COUNTER)
            .tags(tags)
            .register(meterRegistry)
            .increment()
    }

    private fun measureLockTime(tags: MutableSet<Tag>, startMilliseconds: Long) {
        Timer.builder(MonitoringConstants.METRIC_LOCK_TIMER)
            .publishPercentiles(*MonitoringConstants.DEFAULT_PERCENTILES)
            .tags(tags)
            .register(meterRegistry)
            .record(meterRegistry.config().clock().wallTime() - startMilliseconds, TimeUnit.MILLISECONDS)
    }

    private fun defaultTags(actionName: String, cacheName: String): MutableSet<Tag> {
        return mutableSetOf(
            Tag.of(MonitoringConstants.TAG_CACHE_NAME, cacheName),
            Tag.of(MonitoringConstants.TAG_ACTION_NAME, actionName),
        )
    }

    private fun additionalTags(success: Boolean): MutableSet<Tag> {
        return mutableSetOf(
            Tag.of(MonitoringConstants.TAG_RESULT, calculateResultTag(success)),
        )
    }

    private fun calculateResultTag(success: Boolean): String {
        return if (success) {
            MonitoringConstants.TAG_VALUE_SUCCESS
        } else {
            MonitoringConstants.TAG_VALUE_FAILURE
        }
    }
}