package org.service.toyhelloworld.application.port.out

import org.service.toyhelloworld.domain.payment.PaymentEvent


interface SavePaymentPort {
    fun save(paymentEvent: PaymentEvent);
}