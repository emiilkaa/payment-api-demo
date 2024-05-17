package com.example.config

import com.example.config.properties.ExecutorProperties
import org.springframework.beans.factory.BeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.sleuth.instrument.async.LazyTraceExecutor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
@EnableAsync
@EnableConfigurationProperties(ExecutorProperties::class)
class ExecutorConfig(
    val executorProperties: ExecutorProperties
) {

    @Bean
    @ConditionalOnMissingBean(name = ["lockExecutor"])
    fun lockExecutor(beanFactory: BeanFactory): LazyTraceExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = executorProperties.coreSize
        executor.maxPoolSize = executorProperties.maxSize
        executor.threadNamePrefix = "lock_executor-"
        executor.initialize()
        return LazyTraceExecutor(beanFactory, executor)
    }

}