package com.example.model

import com.example.enums.RequestType

abstract class OperationRequest {

    abstract fun requestType(): RequestType

    abstract fun additionalInfo(): MutableMap<String, String>

}
