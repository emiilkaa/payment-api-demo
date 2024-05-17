package com.example.monitoring.service.impl

import com.example.common.LOGGER
import com.example.model.OperationRequest
import com.example.monitoring.MonitoringConstants
import com.example.monitoring.service.MonitoringRequestDataService
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Timer
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class MonitoringRequestDataServiceImpl(
    val meterRegistry: MeterRegistry
) : MonitoringRequestDataService {

    override fun monitoringRequestEnrichment(joinPoint: ProceedingJoinPoint, operationRequest: OperationRequest): Any? {
        val start = meterRegistry.config().clock().wallTime()
        try {
            val response = joinPoint.proceed()
            monitoring(operationRequest, true, start)
            return response
        } catch (e: Throwable) {
            monitoring(operationRequest, false, start)
            throw e
        }
    }

    private fun monitoring(request: OperationRequest, success: Boolean, start: Long) {
        try {
            val tags = requestTags(request, success)
            measureEnrichmentTime(tags, start)
        } catch (ex: Exception) {
            LOGGER.MONITORING.error("Operation monitoring error occurs", ex)
        }
    }


    private fun measureEnrichmentTime(
        tags: Set<Tag>,
        startMilliseconds: Long
    ) {
        Timer.builder(MonitoringConstants.METRIC_ENRICHMENT_TIMER)
            .tags(tags)
            .register(meterRegistry)
            .record(meterRegistry.config().clock().wallTime() - startMilliseconds, TimeUnit.MILLISECONDS)
    }


    private fun requestTags(request: OperationRequest, success: Boolean): MutableSet<Tag> {
        return mutableSetOf(
            Tag.of(MonitoringConstants.TAG_REQUEST_TYPE, request.requestType().name),
            Tag.of(
                MonitoringConstants.TAG_RESULT,
                if (success) MonitoringConstants.TAG_VALUE_SUCCESS else MonitoringConstants.TAG_VALUE_FAILURE
            )
        )
    }

}