package org.service.toyhelloworld.adapter.out.persistent.entity

import org.service.toyhelloworld.common.IdempotencyCreator
import org.service.toyhelloworld.domain.ledger.LedgerTransaction
import org.springframework.stereotype.Component

@Component
class JpaLedgerTransactionMapper {

    fun mapToJpaEntity(ledgerTransaction: LedgerTransaction): JpaLedgerTransactionEntity {
        return JpaLedgerTransactionEntity(
            description = "Ledger transaction description",
            referenceType = ledgerTransaction.referenceType.name,
            referenceId = ledgerTransaction.referenceId,
            orderId = ledgerTransaction.orderId,
            idempotencyKey = IdempotencyCreator.create(ledgerTransaction)
        )
    }

}
