package com.example.config

import com.example.model.OperationRequest
import com.example.service.operation.Operation
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig(val applicationContext: ApplicationContext) {

    @Bean
    @Suppress("UNCHECKED_CAST")
    fun operations(): List<Operation<OperationRequest>> {
        return applicationContext.getBeansOfType(Operation::class.java)
            .values
            .filterIsInstance(Operation::class.java)
            .map { it as Operation<OperationRequest> }
    }

}