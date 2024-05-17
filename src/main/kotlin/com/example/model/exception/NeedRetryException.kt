package com.example.model.exception

class NeedRetryException(
    details: String,
    cause: Throwable? = null
) : RuntimeException(details, cause)