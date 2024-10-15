package org.service.toyhelloworld.application.port.out


interface PaymentValidationPort {
    fun isValid(orderId: String, amount: Long)
}