package com.example.monitoring.service.impl

import com.example.common.LOGGER
import com.example.entity.Request
import com.example.model.OperationData
import com.example.model.OperationRequest
import com.example.model.OperationResponse
import com.example.monitoring.MonitoringConstants
import com.example.monitoring.service.MonitoringOperationService
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Timer
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class MonitoringOperationServiceImpl(
    private val meterRegistry: MeterRegistry
): MonitoringOperationService {

    override fun monitoringOperation(joinPoint: ProceedingJoinPoint, message: OperationData<OperationRequest>): Any? {
        val start = meterRegistry.config().clock().wallTime()

        try {
            val response = joinPoint.proceed()
            if (response is OperationResponse) {
                monitoring(message, response, start)
            }
            return response
        } catch (e: Throwable) {
            monitoringError(message, start)
            throw e
        }
    }

    private fun monitoring(message: OperationData<OperationRequest>, response: OperationResponse, start: Long) {
        try {
            val request = message.request
            val tags = requestTags(request).union(responseTags(response))
            measureOperationTime(tags, start)
        } catch (ex: Exception) {
            LOGGER.MONITORING.error("Operation monitoring error occurs", ex)
        }
    }

    private fun monitoringError(message: OperationData<OperationRequest>, start: Long) {
        try {
            val request = message.request
            val tags = requestTags(request)
            tags.add(Tag.of(MonitoringConstants.TAG_RESULT, MonitoringConstants.TAG_VALUE_FAILURE))
            measureOperationTime(tags, start)
        } catch (ex: Exception) {
            LOGGER.MONITORING.error("Operation monitoring error occurs", ex)
        }
    }

    private fun measureOperationTime(
        tags: Set<Tag>,
        startMilliseconds: Long
    ) {
        Timer.builder(MonitoringConstants.METRIC_OPERATION_TIMER)
            .tags(tags)
            .register(meterRegistry)
            .record(meterRegistry.config().clock().wallTime() - startMilliseconds, TimeUnit.MILLISECONDS)
    }

    private fun requestTags(request: Request): MutableSet<Tag> {
        return mutableSetOf(
            Tag.of(MonitoringConstants.TAG_REQUEST_TYPE, request.requestType.name)
        )
    }

    private fun responseTags(response: OperationResponse): MutableSet<Tag> {
        return mutableSetOf(
            Tag.of(
                MonitoringConstants.TAG_RESULT,
                if (response.success) MonitoringConstants.TAG_VALUE_SUCCESS else MonitoringConstants.TAG_VALUE_FAILURE
            )
        )
    }

}