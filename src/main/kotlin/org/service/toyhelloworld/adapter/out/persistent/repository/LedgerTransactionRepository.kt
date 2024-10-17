package org.service.toyhelloworld.adapter.out.persistent.repository

import org.service.toyhelloworld.domain.payment.PaymentEventMessage


interface LedgerTransactionRepository {
    fun isExist(message: PaymentEventMessage): Boolean
}