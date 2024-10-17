package org.service.toyhelloworld.adapter.out.persistent.repository

import org.service.toyhelloworld.domain.payment.PaymentEventMessage
import org.service.toyhelloworld.domain.wallet.WalletTransaction


interface WalletTransactionRepository {
    fun isExist(paymentEventMessage: PaymentEventMessage): Boolean
    fun save(walletTransactions: List<WalletTransaction>)
}