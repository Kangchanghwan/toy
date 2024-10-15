package org.service.toyhelloworld.adapter.`in`.web.api

import ApiResponse
import org.service.toyhelloworld.adapter.`in`.request.TossPaymentConfirmRequest
import org.service.toyhelloworld.application.port.`in`.PaymentConfirmCommand
import org.service.toyhelloworld.application.port.`in`.PaymentConfirmUseCase
import org.service.toyhelloworld.common.WebAdapter
import org.service.toyhelloworld.domain.payment.PaymentConfirmationResult
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@WebAdapter
@RequestMapping("/v1/toss")
@RestController
class TossPaymentController(
    private val paymentConfirmUseCase: PaymentConfirmUseCase,
) {

    @PostMapping("/confirm")
    fun confirm(@RequestBody request: TossPaymentConfirmRequest): ResponseEntity<ApiResponse<PaymentConfirmationResult>> {
        val command = PaymentConfirmCommand(
            paymentKey = request.paymentKey,
            orderId = request.orderId,
            amount = request.amount
        )
        val paymentConfirmationResult = paymentConfirmUseCase.confirm(command)
        return ResponseEntity.ok().body(ApiResponse.with(HttpStatus.OK, "", paymentConfirmationResult))
    }
}