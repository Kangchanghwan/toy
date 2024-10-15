package org.service.toyhelloworld.application.port.`in`

import org.service.toyhelloworld.domain.payment.PaymentConfirmationResult

interface PaymentConfirmUseCase {

    fun confirm(command: PaymentConfirmCommand): PaymentConfirmationResult
}