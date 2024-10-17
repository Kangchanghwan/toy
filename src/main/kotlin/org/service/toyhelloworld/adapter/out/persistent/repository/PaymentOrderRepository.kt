package org.service.toyhelloworld.adapter.out.persistent.repository

import org.service.toyhelloworld.application.port.out.PaymentStatusUpdateCommand
import org.service.toyhelloworld.domain.payment.PaymentOrder

interface PaymentOrderRepository {
    fun updatePaymentStatusToExecuting(orderId: String, paymentKey: String)
    fun updatePaymentStatus(command: PaymentStatusUpdateCommand)
    fun isValid(orderId: String, amount: Long)
    fun getPaymentOrders(orderId: String): List<PaymentOrder>
}