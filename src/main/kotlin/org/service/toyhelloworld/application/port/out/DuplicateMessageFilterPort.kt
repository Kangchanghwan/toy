package org.service.toyhelloworld.application.port.out

import org.service.toyhelloworld.domain.payment.PaymentEventMessage


interface DuplicateMessageFilterPort {
    fun isAlreadyProcess(paymentEventMessage: PaymentEventMessage): Boolean
}