package org.service.toyhelloworld.domain.ledger

data class LedgerTransaction(
    val referenceType: ReferenceType,
    val referenceId: Long,
    val orderId: String
)


enum class ReferenceType {
    PAYMENT_ORDER
}