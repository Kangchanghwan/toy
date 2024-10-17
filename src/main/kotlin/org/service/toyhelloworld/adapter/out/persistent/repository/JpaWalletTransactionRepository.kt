package org.service.toyhelloworld.adapter.out.persistent.repository

import jakarta.transaction.Transactional
import org.service.toyhelloworld.adapter.out.persistent.entity.JpaWalletTransactionEntity
import org.service.toyhelloworld.adapter.out.persistent.entity.JpaWalletTransactionMapper
import org.service.toyhelloworld.domain.payment.PaymentEventMessage
import org.service.toyhelloworld.domain.wallet.WalletTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
class JpaWalletTransactionRepository(
    private val springDataJpaWalletTransactionRepository: SpringDataJpaWalletTransactionRepository,
    private val jpaWalletTransactionMapper: JpaWalletTransactionMapper
) : WalletTransactionRepository{
    override fun isExist(paymentEventMessage: PaymentEventMessage): Boolean {
        return springDataJpaWalletTransactionRepository.existsByOrderId(paymentEventMessage.orderId())
    }
    override fun save(walletTransactions: List<WalletTransaction>) {
        springDataJpaWalletTransactionRepository.saveAll(
            walletTransactions.map { jpaWalletTransactionMapper.mapToJpaEntity(it) }
        )
    }
}

interface SpringDataJpaWalletTransactionRepository : JpaRepository<JpaWalletTransactionEntity, Long> {
    fun existsByOrderId(orderId: String): Boolean
}