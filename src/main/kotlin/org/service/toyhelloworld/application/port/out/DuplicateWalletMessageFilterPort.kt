package org.service.toyhelloworld.application.port.out

import org.service.toyhelloworld.domain.payment.PaymentEventMessage


interface DuplicateWalletMessageFilterPort {
    fun isAlreadyProcess(paymentEventMessage: PaymentEventMessage): Boolean
}