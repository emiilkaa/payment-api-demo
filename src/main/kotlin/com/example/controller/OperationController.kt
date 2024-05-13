package com.example.controller

import com.example.model.CancelRequest
import com.example.model.OperationResponse
import com.example.model.PayRequest
import com.example.service.OperationService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/operations")
class OperationController(
    val operationService: OperationService
) {

    @PostMapping(PAY)
    fun pay(@RequestBody @Valid payRequest: PayRequest): ResponseEntity<OperationResponse> {
        return ResponseEntity.ok(operationService.handleOperation(payRequest))
    }

    @PostMapping(CANCEL)
    fun cancel(@RequestBody @Valid cancelRequest: CancelRequest): ResponseEntity<OperationResponse> {
        return ResponseEntity.ok(operationService.handleOperation(cancelRequest))
    }

    companion object {
        const val PAY: String = "/pay"
        const val CANCEL: String = "/cancel"
    }

}