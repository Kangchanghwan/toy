package org.service.toyhelloworld.adapter.`in`.web.toss.executor

import org.service.toyhelloworld.application.port.`in`.PaymentConfirmCommand
import org.service.toyhelloworld.domain.payment.PaymentExecutionResult


interface PaymentExecutor {
    fun execute(command: PaymentConfirmCommand): PaymentExecutionResult
}