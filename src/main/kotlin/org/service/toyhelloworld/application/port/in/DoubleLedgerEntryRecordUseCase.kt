package org.service.toyhelloworld.application.port.`in`

import org.service.toyhelloworld.domain.ledger.LedgerEventMessage
import org.service.toyhelloworld.domain.payment.PaymentEventMessage


interface DoubleLedgerEntryRecordUseCase {
    fun recordDoubleLegerEntry(message: PaymentEventMessage): LedgerEventMessage
}