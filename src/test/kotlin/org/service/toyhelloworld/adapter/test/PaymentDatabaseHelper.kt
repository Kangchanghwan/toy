package org.service.toyhelloworld.adapter.test

import org.service.toyhelloworld.domain.payment.PaymentEvent


interface PaymentDatabaseHelper {
    fun getPayments(orderId: String): PaymentEvent?
    fun clean()
}