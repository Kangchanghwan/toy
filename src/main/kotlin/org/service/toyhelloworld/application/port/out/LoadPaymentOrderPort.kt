package org.service.toyhelloworld.application.port.out

import org.service.toyhelloworld.domain.payment.PaymentOrder


interface LoadPaymentOrderPort {
    fun getPaymentOrders(orderId:String): List<PaymentOrder>
}