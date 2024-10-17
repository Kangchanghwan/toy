package org.service.toyhelloworld.adapter.out.persistent.repository

import org.service.toyhelloworld.adapter.out.persistent.entity.JpaLedgerTransactionEntity
import org.service.toyhelloworld.domain.payment.PaymentEventMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
class JpaLedgerTransactionRepository(
    private val springDataJpaLedgerTransactionRepository: SpringDataJpaLedgerTransactionRepository
) : LedgerTransactionRepository {
    override fun isExist(message: PaymentEventMessage): Boolean {
      return springDataJpaLedgerTransactionRepository.existsByOrderId(message.orderId())
    }
}

interface SpringDataJpaLedgerTransactionRepository: JpaRepository<JpaLedgerTransactionEntity, Long> {
    fun existsByOrderId(orderId: String): Boolean
}