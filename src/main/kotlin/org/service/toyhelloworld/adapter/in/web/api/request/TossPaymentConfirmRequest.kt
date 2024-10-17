package org.service.toyhelloworld.adapter.`in`.web.api.request

data class TossPaymentConfirmRequest(
    val paymentKey: String,
    val orderId: String,
    val amount: Long
) {}