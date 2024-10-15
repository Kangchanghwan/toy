package org.service.toyhelloworld.application.port.out

import org.service.toyhelloworld.application.port.`in`.PaymentConfirmCommand
import org.service.toyhelloworld.domain.payment.PaymentExecutionResult

interface PaymentExecutionPort {
    fun execute(command: PaymentConfirmCommand): PaymentExecutionResult
}