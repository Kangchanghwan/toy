package org.service.toyhelloworld.domain.wallet

import org.service.toyhelloworld.domain.wallet.ReferenceType
import org.service.toyhelloworld.domain.wallet.TransactionType

data class WalletTransaction(
    val walletId: Long,
    val amount: Long,
    val type: TransactionType,
    val referenceId: Long,
    val referenceType: ReferenceType,
    val orderId: String
)
