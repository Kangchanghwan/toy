package org.service.toyhelloworld.adapter.`in`.request

data class TossPaymentConfirmRequest(
    val paymentKey: String,
    val orderId: String,
    val amount: Long
) {}