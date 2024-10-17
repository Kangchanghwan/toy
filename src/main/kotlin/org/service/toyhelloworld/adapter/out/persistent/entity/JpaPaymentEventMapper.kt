package org.service.toyhelloworld.adapter.out.persistent.entity

import org.service.toyhelloworld.domain.payment.PaymentEvent
import org.service.toyhelloworld.domain.payment.PaymentOrder
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
    fun mapToDomainEntity(entity: JpaPaymentEventEntity, jpaPaymentOrderEntities: List<JpaPaymentOrderEntity> = listOf()): PaymentEvent{
        return PaymentEvent(
            buyerId = entity.buyerId,
            orderName = entity.orderName,
            id = entity.id,
            paymentKey = entity.paymentKey,
            orderId = entity.orderId ?: "",
            paymentType = entity.type,
            paymentMethod = entity.method,
            approvedAt = entity.approvedAt,
            paymentOrders = jpaPaymentOrderEntities.map {
                PaymentOrder(
                    id = it.id,
                    paymentEventId = entity.id,
                    sellerId = it.sellerId,
                    productId = it.productId,
                    orderId = it.orderId,
                    amount = it.amount.toLong(),
                    paymentStatus = it.paymentOrderStatus,
                    isLegerUpdated = it.ledgerUpdated,
                    isWalletUpdated = it.walletUpdated,
                )
            }
        )
    }
}