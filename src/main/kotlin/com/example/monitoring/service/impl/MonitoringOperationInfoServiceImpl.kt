package com.example.monitoring.service.impl

import com.example.common.LOGGER
import com.example.monitoring.MonitoringConstants
import com.example.monitoring.service.MonitoringOperationInfoService
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Timer
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class MonitoringOperationInfoServiceImpl(
    val meterRegistry: MeterRegistry
) : MonitoringOperationInfoService {

    override fun monitoringOperationInfo(joinPoint: ProceedingJoinPoint, requestType: String): Any? {
        val start = meterRegistry.config().clock().wallTime()
        try {
            val response = joinPoint.proceed()
            monitoring(requestType, true, start)
            return response
        } catch (e: Throwable) {
            monitoring(requestType, false, start)
            throw e
        }
    }

    private fun monitoring(requestType: String, success: Boolean, start: Long) {
        try {
            val tags = requestTags(requestType, success)
            measureEnrichmentTime(tags, start)
        } catch (ex: Exception) {
            LOGGER.MONITORING.error("Operation info monitoring error occurs", ex)
        }
    }


    private fun measureEnrichmentTime(
        tags: Set<Tag>,
        startMilliseconds: Long
    ) {
        Timer.builder(MonitoringConstants.METRIC_OPERATION_INFO_TIMER)
            .tags(tags)
            .register(meterRegistry)
            .record(meterRegistry.config().clock().wallTime() - startMilliseconds, TimeUnit.MILLISECONDS)
    }


    private fun requestTags(requestType: String, success: Boolean): MutableSet<Tag> {
        return mutableSetOf(
            Tag.of(MonitoringConstants.TAG_REQUEST_TYPE, requestType),
            Tag.of(
                MonitoringConstants.TAG_RESULT,
                if (success) MonitoringConstants.TAG_VALUE_SUCCESS else MonitoringConstants.TAG_VALUE_FAILURE
            )
        )
    }

}