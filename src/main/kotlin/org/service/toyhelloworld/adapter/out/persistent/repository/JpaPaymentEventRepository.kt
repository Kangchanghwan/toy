package org.service.toyhelloworld.adapter.out.persistent.repository

import jakarta.transaction.Transactional
import org.service.toyhelloworld.adapter.out.persistent.entity.JpaPaymentEventEntity
import org.service.toyhelloworld.adapter.out.persistent.entity.JpaPaymentEventMapper
import org.service.toyhelloworld.adapter.out.persistent.entity.JpaPaymentOrderEntity
import org.service.toyhelloworld.domain.payment.PaymentEvent
import org.service.toyhelloworld.domain.payment.PaymentMethod
import org.service.toyhelloworld.domain.payment.PaymentType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class JpaPaymentEventRepository(
    private val jpaPaymentEventMapper: JpaPaymentEventMapper,
    private val springDataJpaPaymentOrderRepository: SpringDataJpaPaymentOrderRepository,
    private val springDataJpaPaymentEventRepository: SpringDataJpaPaymentEventRepository,
) : PaymentEventRepository {

    @Transactional
    override fun save(paymentEvent: PaymentEvent) {
        val paymentEventEntity = insertPaymentEvent(paymentEvent)
        insertPaymentOrders(paymentEvent, paymentEventEntity)
    }

    private fun insertPaymentOrders(
        paymentEvent: PaymentEvent,
        paymentEventEntity: JpaPaymentEventEntity,
    ) {
        val jpaPaymentOrderEntities = createPaymentOrders(paymentEvent, paymentEventEntity)
        springDataJpaPaymentOrderRepository.saveAll(jpaPaymentOrderEntities)
    }

    private fun insertPaymentEvent(paymentEvent: PaymentEvent) =
        springDataJpaPaymentEventRepository.save(
            jpaPaymentEventMapper.mapToEntity(paymentEvent)
        )

    private fun createPaymentOrders(
        paymentEvent: PaymentEvent,
        paymentEventEntity: JpaPaymentEventEntity,
    ): List<JpaPaymentOrderEntity> {
        return paymentEvent.paymentOrders.map { paymentOrder ->
            JpaPaymentOrderEntity(
                paymentEvent = paymentEventEntity,
                sellerId = paymentOrder.sellerId,
                orderId = paymentOrder.orderId,
                productId = paymentOrder.productId,
                amount = paymentOrder.amount.toBigDecimal(),
                paymentOrderStatus = paymentOrder.paymentStatus
            )
        }
    }
}

interface SpringDataJpaPaymentEventRepository : JpaRepository<JpaPaymentEventEntity, Long> {
    @Modifying
    @Query("UPDATE JpaPaymentEventEntity e SET e.paymentKey=:paymentKey  WHERE e.orderId =:orderId")
    fun updatePaymentKey(orderId: String, paymentKey: String)

    @Modifying
    @Query("UPDATE JpaPaymentEventEntity e SET e.orderName=:orderName, e.method=:method, e.approvedAt=:approvedAt,e.type=:type WHERE e.orderId=:orderId")
    fun updatePaymentExtraDetails(
        orderName: String,
        method: PaymentMethod,
        approvedAt: LocalDateTime,
        type: PaymentType,
        orderId: String,
    )
}
