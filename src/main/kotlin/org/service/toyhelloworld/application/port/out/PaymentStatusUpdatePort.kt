package org.service.toyhelloworld.application.port.out

interface PaymentStatusUpdatePort {
    fun updatePaymentStatusToExecuting(orderId: String, paymentKey: String)
    fun updatePaymentStatus(command: PaymentStatusUpdateCommand)
}