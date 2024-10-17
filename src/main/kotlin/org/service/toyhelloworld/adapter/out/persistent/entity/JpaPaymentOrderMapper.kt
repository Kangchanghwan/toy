package org.service.toyhelloworld.adapter.out.persistent.entity

import org.service.toyhelloworld.domain.payment.PaymentOrder
import org.springframework.stereotype.Component

@Component
class JpaPaymentOrderMapper {

    fun mapToDomainEntity(jpaPaymentOrderEntity: JpaPaymentOrderEntity): PaymentOrder{
        return PaymentOrder(
            id = jpaPaymentOrderEntity.id,
            sellerId = jpaPaymentOrderEntity.sellerId,
            amount = jpaPaymentOrderEntity.amount.toLong(),
            orderId = jpaPaymentOrderEntity.orderId,
        )
    }
}