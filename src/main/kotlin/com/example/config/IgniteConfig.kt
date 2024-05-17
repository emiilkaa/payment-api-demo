package com.example.config

import com.example.common.LockName
import com.example.config.properties.IgniteProperties
import org.apache.ignite.Ignition
import org.apache.ignite.cache.CacheAtomicityMode
import org.apache.ignite.client.ClientCacheConfiguration
import org.apache.ignite.client.IgniteClient
import org.apache.ignite.configuration.ClientConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit
import javax.cache.expiry.CreatedExpiryPolicy
import javax.cache.expiry.Duration

@Configuration
@EnableConfigurationProperties(IgniteProperties::class)
class IgniteConfig(
    val igniteProperties: IgniteProperties,
) {

    @Bean
    @ConditionalOnMissingBean(IgniteClient::class)
    fun igniteClient(): IgniteClient {
        val addresses: Array<String> = igniteProperties.addresses.split(",").toTypedArray()
        return startClient(addresses)
    }

    @Bean
    fun terminalLockClientCacheConfiguration(): ClientCacheConfiguration {
        return getClientCacheConfiguration(
            LockName.TERMINAL_ID_LOCK_NAME, Duration(TimeUnit.SECONDS, igniteProperties.expirePolicyDuration)
        )
    }

    private fun getClientCacheConfiguration(name: String, duration: Duration): ClientCacheConfiguration {
        val clientCacheConfiguration = ClientCacheConfiguration()
        clientCacheConfiguration.name = name
        clientCacheConfiguration.expiryPolicy = CreatedExpiryPolicy.factoryOf(duration).create()
        clientCacheConfiguration.isStatisticsEnabled = true
        clientCacheConfiguration.atomicityMode = CacheAtomicityMode.TRANSACTIONAL
        return clientCacheConfiguration
    }

    private fun startClient(hosts: Array<String>): IgniteClient {
        val clientConfiguration = ClientConfiguration()
        clientConfiguration.setAddresses(*hosts)
        clientConfiguration.transactionConfiguration.defaultTxTimeout = 200L
        clientConfiguration.timeout = DEFAULT_TIMEOUT
        return Ignition.startClient(clientConfiguration)
    }

    companion object {
        const val DEFAULT_TIMEOUT = 3000
    }
}
