package org.service.toyhelloworld.domain.payment

data class CheckOutResult(
    val amount: Long,
    val orderId: String,
    val orderName: String
)