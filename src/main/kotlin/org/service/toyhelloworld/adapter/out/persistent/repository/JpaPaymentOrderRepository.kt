package org.service.toyhelloworld.adapter.out.persistent.repository

import jakarta.persistence.Tuple
import jakarta.transaction.Transactional
import org.service.toyhelloworld.adapter.out.persistent.entity.JpaPaymentOrderEntity
import org.service.toyhelloworld.adapter.out.persistent.entity.JpaOutBoxEntity
import org.service.toyhelloworld.adapter.out.persistent.entity.JpaPaymentOrderHistoryEntity
import org.service.toyhelloworld.adapter.out.persistent.entity.JpaPaymentOrderMapper
import org.service.toyhelloworld.adapter.out.persistent.exception.PaymentAlreadyProcessedException
import org.service.toyhelloworld.adapter.out.persistent.exception.PaymentValidationException
import org.service.toyhelloworld.application.port.out.PaymentStatusUpdateCommand
import org.service.toyhelloworld.common.objectMapper
import org.service.toyhelloworld.domain.payment.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
class JpaPaymentOrderRepository(
    private val springDataJpaPaymentOrderRepository: SpringDataJpaPaymentOrderRepository,
    private val springDataJpaPaymentOrderHistoryRepository: SpringDataJpaPaymentOrderHistoryRepository,
    private val springDataJpaPaymentEventRepository: SpringDataJpaPaymentEventRepository,
    private val springDataJpaOutBoxRepository: SpringDataJpaOutBoxRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val jpaPaymentOrderMapper: JpaPaymentOrderMapper
) : PaymentOrderRepository {

    @Transactional
    override fun updatePaymentStatusToExecuting(orderId: String, paymentKey: String) {
        val orderIdWithStatusList = checkPreviousPaymentOrderStatus(orderId)
        insertPaymentOrderHistories(orderIdWithStatusList, PaymentStatus.EXECUTING, "PAYMENT_CONFIRMATION_START")
        springDataJpaPaymentOrderRepository.updatePaymentOrderStatus(orderId, PaymentStatus.EXECUTING)
        springDataJpaPaymentEventRepository.updatePaymentKey(orderId, paymentKey)
    }

    @Transactional
    override fun updatePaymentStatus(command: PaymentStatusUpdateCommand) {
        return when (command.status) {
            PaymentStatus.SUCCESS -> updatePaymentStatusToSuccess(command)
            PaymentStatus.FAILURE -> updatePaymentStatusToFailure(command)
            PaymentStatus.UNKNOWN -> updatePaymentStatusToUnknown(command)
            else -> error("결제 상태 (status: ${command.status})는 올바르지 않은 결제 상태입니다.")
        }
    }

    override fun isValid(orderId: String, amount: Long) {
        val totalAmount = springDataJpaPaymentOrderRepository.selectPaymentOrderTotalAmount(orderId)
        if (totalAmount != amount)
            throw PaymentValidationException("결제 (orderId: ${orderId}) 에서 금액 (amount: $amount)이 올바르지 않습니다.")
    }

    override fun getPaymentOrders(orderId: String): List<PaymentOrder> {
        return springDataJpaPaymentOrderRepository.findByOrderId(orderId)
            .map { jpaPaymentOrderMapper.mapToDomainEntity(it) }
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    override fun complete(paymentEvent: PaymentEvent) {
        when {
            paymentEvent.isWalletUpdateDone() -> springDataJpaPaymentOrderRepository.updatePaymentOrderWalletUpdatedComplete(paymentEvent.orderId)
            paymentEvent.isLedgerUpdateDone() -> springDataJpaPaymentOrderRepository.updatePaymentOrderLedgerUpdatedComplete(paymentEvent.orderId)
        }
    }

    private fun insertPaymentOrderHistories(
        orderIdWithStatusList: List<Tuple>,
        status: PaymentStatus,
        reason: String,
    ) {
        val jpaPaymentOrderHistoryEntities = orderIdWithStatusList.map {
            JpaPaymentOrderHistoryEntity(
                paymentOrderId = it.get(0,Long::class.java),
                previousStatus = it.get(1, PaymentStatus::class.java),
                newStatus = status,
                reason = reason
            )
        }
        springDataJpaPaymentOrderHistoryRepository.saveAll(jpaPaymentOrderHistoryEntities)
    }

    private fun checkPreviousPaymentOrderStatus(orderId: String): List<Tuple> {
        return springDataJpaPaymentOrderRepository.selectPaymentOrderStatus(orderId)
            .asSequence()
            .map { tuple ->
                when (tuple.get(1) as PaymentStatus) {
                    PaymentStatus.NOT_STARTED,
                    PaymentStatus.UNKNOWN,
                    PaymentStatus.EXECUTING,
                    -> tuple

                    PaymentStatus.SUCCESS -> throw PaymentAlreadyProcessedException(
                        message = "이미 처리 성공한 결제입니다.",
                        status = PaymentStatus.SUCCESS
                    )

                    PaymentStatus.FAILURE -> throw PaymentAlreadyProcessedException(
                        message = "이미 처리 실패한 결제입니다.",
                        status = PaymentStatus.FAILURE
                    )
                }
            }.toList()
    }

    private fun updatePaymentStatusToSuccess(command: PaymentStatusUpdateCommand) {
        val orderIdWithStatusList = springDataJpaPaymentOrderRepository.selectPaymentOrderStatus(command.orderId)
        insertPaymentOrderHistories(orderIdWithStatusList, command.status, "PAYMENT_CONFIRMATION_DONE")
        springDataJpaPaymentOrderRepository.updatePaymentOrderStatus(command.orderId, command.status)
        springDataJpaPaymentEventRepository.updatePaymentExtraDetails(
            orderName = command.extraDetails!!.orderName,
            method = command.extraDetails.method,
            approvedAt = command.extraDetails.approvedAt,
            type = command.extraDetails.type,
            orderId = command.orderId
        )
        val paymentEventMessage = createPaymentMessage(command)
        insertOutbox(paymentEventMessage)
        applicationEventPublisher.publishEvent(paymentEventMessage)
    }

    private fun insertOutbox(paymentEventMessage: PaymentEventMessage) {
        val jpaOutBoxEntity = JpaOutBoxEntity(
            idempotencyKey = paymentEventMessage.payload["orderId"] as String,
            partitionKey = 0,
            type = paymentEventMessage.type.name,
            payload = objectMapper.writeValueAsString(paymentEventMessage.payload),
            metadata = objectMapper.writeValueAsString(paymentEventMessage.metadata)
        )
        springDataJpaOutBoxRepository.save(jpaOutBoxEntity)
    }

    private fun createPaymentMessage(command: PaymentStatusUpdateCommand): PaymentEventMessage {
        return PaymentEventMessage(
            type = PaymentEventMessageType.PAYMENT_CONFIRMATION_SUCCESS,
            payload = mapOf(
                "orderId" to command.orderId
            ),
        )
    }
    private fun updatePaymentStatusToFailure(command: PaymentStatusUpdateCommand) {
        val orderIdWithStatusList = springDataJpaPaymentOrderRepository.selectPaymentOrderStatus(command.orderId)
        insertPaymentOrderHistories(orderIdWithStatusList, command.status, command.failure.toString())
        springDataJpaPaymentOrderRepository.updatePaymentOrderStatus(command.orderId, command.status)
    }

    private fun updatePaymentStatusToUnknown(command: PaymentStatusUpdateCommand) {
        val orderIdWithStatusList = springDataJpaPaymentOrderRepository.selectPaymentOrderStatus(command.orderId)
        insertPaymentOrderHistories(orderIdWithStatusList, command.status, command.failure.toString())
        springDataJpaPaymentOrderRepository.updatePaymentOrderStatus(command.orderId, command.status)
        springDataJpaPaymentOrderRepository.incrementPaymentOrderFailedCount(command.orderId)
    }
}

interface SpringDataJpaPaymentOrderRepository : JpaRepository<JpaPaymentOrderEntity, Long> {

    @Query("SELECT e.id, e.paymentOrderStatus FROM JpaPaymentOrderEntity e WHERE e.orderId=:orderId")
    fun selectPaymentOrderStatus(orderId: String): List<Tuple>

    @Modifying
    @Query("UPDATE JpaPaymentOrderEntity e SET e.paymentOrderStatus=:status WHERE e.orderId=:orderId")
    fun updatePaymentOrderStatus(orderId: String, status: PaymentStatus)

    @Modifying
    @Query("UPDATE JpaPaymentOrderEntity e SET e.failedCount = e.failedCount + 1 WHERE e.orderId=:orderId")
    fun incrementPaymentOrderFailedCount(orderId: String)

    @Query("SELECT SUM(e.amount) from JpaPaymentOrderEntity e WHERE e.orderId=:orderId")
    fun selectPaymentOrderTotalAmount(orderId: String): Long

    @Modifying
    @Query("UPDATE JpaPaymentOrderEntity e SET e.walletUpdated = TRUE WHERE e.orderId=:orderId")
    fun updatePaymentOrderWalletUpdatedComplete(orderId: String)

    @Modifying
    @Query("UPDATE JpaPaymentOrderEntity e SET e.ledgerUpdated = TRUE WHERE e.orderId=:orderId")
    fun updatePaymentOrderLedgerUpdatedComplete(orderId: String)

    fun findByOrderId(orderId: String): List<JpaPaymentOrderEntity>
}