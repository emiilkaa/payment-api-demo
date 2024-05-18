package com.example.monitoring

object MonitoringConstants {

    const val METRIC_LOCK_COUNTER = "lock_counter"

    const val METRIC_OPERATION_TIMER = "operation_timer"
    const val METRIC_ENRICHMENT_TIMER = "enrichment_timer"
    const val METRIC_OPERATION_INFO_TIMER = "operation_info_timer"
    const val METRIC_LOCK_TIMER = "lock_timer"
    const val METRIC_IGNITE_TIMER = "ignite_timer"

    const val TAG_REQUEST_TYPE = "type"
    const val TAG_RESULT = "result"
    const val TAG_CACHE_NAME = "cache_name"
    const val TAG_ACTION_NAME = "action_name"

    const val TAG_VALUE_SUCCESS = "success"
    const val TAG_VALUE_FAILURE = "failure"

    const val LOCK_ACTION = "LOCK"
    const val UNLOCK_ACTION = "UNLOCK"

    const val BY_REQUEST_ID = "BY_REQUEST_ID"
    const val BY_PAYMENT_ID = "BY_PAYMENT_ID"

    val DEFAULT_PERCENTILES = doubleArrayOf(0.5, 0.95, 0.99)

}