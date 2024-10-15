package org.service.toyhelloworld.application.port.`in`

data class CheckOutCommand(
    val cartId: Long,
    val buyerId: Long,
    val productIds: List<Long>,
    val idempotencyKey: String
)
