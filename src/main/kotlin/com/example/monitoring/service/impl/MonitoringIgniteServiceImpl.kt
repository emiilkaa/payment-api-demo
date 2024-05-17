package com.example.monitoring.service.impl

import com.example.common.LOGGER
import com.example.monitoring.MonitoringConstants
import com.example.monitoring.service.MonitoringIgniteService
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Timer
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class MonitoringIgniteServiceImpl(
    val meterRegistry: MeterRegistry
) : MonitoringIgniteService {

    override fun monitoringCache(joinPoint: ProceedingJoinPoint, actionName: String, cacheName: String): Any? {
        val start = meterRegistry.config().clock().wallTime()
        try {
            val result = joinPoint.proceed()
            monitoring(start, actionName, cacheName, true)
            return result
        } catch (e: Throwable) {
            LOGGER.MONITORING.error("Exception during $actionName. Message: {}", e.message)
            monitoring(start, actionName, cacheName, false)
            throw e
        }
    }

    private fun monitoring(start: Long, actionName: String, cacheName: String, success: Boolean) {
        try {
            val tags = generateTags(actionName, cacheName, success)
            measureLockRequestTime(tags, start)
        } catch (ex: Exception) {
            LOGGER.MONITORING.error("Exception lock monitoring", ex)
        }
    }


    private fun measureLockRequestTime(tags: MutableSet<Tag>, startMilliseconds: Long) {
        Timer.builder(MonitoringConstants.METRIC_IGNITE_TIMER)
            .tags(tags)
            .register(meterRegistry)
            .record(meterRegistry.config().clock().wallTime() - startMilliseconds, TimeUnit.MILLISECONDS)
    }

    private fun generateTags(actionName: String, cacheName: String, success: Boolean): MutableSet<Tag> {
        return mutableSetOf(
            Tag.of(MonitoringConstants.TAG_CACHE_NAME, cacheName),
            Tag.of(MonitoringConstants.TAG_ACTION_NAME, actionName),
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