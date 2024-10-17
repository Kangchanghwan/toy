package org.service.toyhelloworld.application.port.out

import org.service.toyhelloworld.domain.payment.PaymentEvent

interface PaymentCompletePort {
    fun complete(paymentEvent: PaymentEvent)
}