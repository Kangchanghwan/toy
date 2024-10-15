package org.service.toyhelloworld.domain.payment

data class Product(
    val id: Long,
    val amount: Long,
    val quantity: Int,
    val name: String,
    val sellerId: Long
)