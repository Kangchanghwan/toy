package org.service.toyhelloworld.adapter.out.persistent.entity

import org.service.toyhelloworld.domain.payment.PaymentEvent
import org.springframework.stereotype.Component

@Component
class JpaPaymentEventMapper {
    fun mapToEntity(paymentEvent: PaymentEvent): JpaPaymentEventEntity{
        return JpaPaymentEventEntity(
            buyerId = paymentEvent.buyerId,
            orderName = paymentEvent.orderName,
            orderId = paymentEvent.orderId
        )
    }
}