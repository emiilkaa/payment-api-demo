package com.example.config

import com.example.config.properties.RetryProperties
import com.example.model.exception.NeedRetryException
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.backoff.BackOffPolicy
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate

@Configuration
@EnableConfigurationProperties(RetryProperties::class)
class RetryTemplateConfig(val retryProperties: RetryProperties) {

    @Bean
    fun lockRetryTemplate(): RetryTemplate {
        val retryTemplate = RetryTemplate()

        val backOffPolicy = getBackOffPolicy(retryProperties.delay)
        retryTemplate.setBackOffPolicy(backOffPolicy)

        val retryableExceptions: Map<Class<out Throwable?>, Boolean> =
            mapOf(Pair(NeedRetryException::class.java, true))
        val simpleRetryPolicy = SimpleRetryPolicy(retryProperties.retryCount, retryableExceptions)
        retryTemplate.setRetryPolicy(simpleRetryPolicy)

        return retryTemplate
    }

    private fun getBackOffPolicy(delay: Int): BackOffPolicy {
        val fixedBackOffPolicy = FixedBackOffPolicy()
        fixedBackOffPolicy.backOffPeriod = delay.toLong()
        return fixedBackOffPolicy
    }

}
