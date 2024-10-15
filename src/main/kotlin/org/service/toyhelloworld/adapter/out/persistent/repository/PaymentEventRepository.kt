package org.service.toyhelloworld.adapter.out.persistent.repository

import org.service.toyhelloworld.domain.payment.PaymentEvent

interface PaymentEventRepository {
    fun save(paymentEvent: PaymentEvent)
}