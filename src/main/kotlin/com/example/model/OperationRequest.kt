package com.example.model

import com.example.entity.Request
import com.example.enums.RequestType

abstract class OperationRequest {

    abstract fun request(): Request

    abstract fun requestType(): RequestType

    abstract fun additionalInfo(): MutableMap<String, String>

}
