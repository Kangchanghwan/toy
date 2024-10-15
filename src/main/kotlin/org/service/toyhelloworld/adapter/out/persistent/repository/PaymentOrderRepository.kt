package org.service.toyhelloworld.adapter.out.persistent.repository

import org.service.toyhelloworld.application.port.out.PaymentStatusUpdateCommand

interface PaymentOrderRepository {
    fun updatePaymentStatusToExecuting(orderId: String, paymentKey: String)
    fun updatePaymentStatus(command: PaymentStatusUpdateCommand)
    fun isValid(orderId: String, amount: Long)
}