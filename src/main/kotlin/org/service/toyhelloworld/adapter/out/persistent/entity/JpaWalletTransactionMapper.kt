package org.service.toyhelloworld.adapter.out.persistent.entity

import org.service.toyhelloworld.common.IdempotencyCreator
import org.service.toyhelloworld.domain.wallet.WalletTransaction
import org.springframework.stereotype.Component

@Component
class JpaWalletTransactionMapper {

    fun mapToJpaEntity(walletTransaction: WalletTransaction): JpaWalletTransactionEntity {
        return JpaWalletTransactionEntity(
            walletId = walletTransaction.walletId,
            amount = walletTransaction.amount.toBigDecimal(),
            type = walletTransaction.type,
            orderId = walletTransaction.orderId,
            referenceId = walletTransaction.referenceId,
            referenceType = walletTransaction.referenceType.name,
            idempotencyKey = IdempotencyCreator.create(walletTransaction)
        )
    }
}