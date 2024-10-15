package org.service.toyhelloworld.domain.payment

data class PaymentFailure (
    val errorCode: String,
    val message: String
)