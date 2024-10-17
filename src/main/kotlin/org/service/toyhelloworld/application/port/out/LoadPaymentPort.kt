package org.service.toyhelloworld.application.port.out

import org.service.toyhelloworld.domain.payment.PaymentEvent

interface LoadPaymentPort {
    fun getPayment(orderId: String): PaymentEvent
}