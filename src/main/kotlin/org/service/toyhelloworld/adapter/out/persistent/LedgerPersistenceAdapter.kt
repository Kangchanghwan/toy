package org.service.toyhelloworld.adapter.out.persistent

import org.service.toyhelloworld.adapter.out.persistent.repository.JpaLedgerEntryRepository
import org.service.toyhelloworld.adapter.out.persistent.repository.LedgerTransactionRepository
import org.service.toyhelloworld.application.port.out.DuplicateLegerMessageFilterPort
import org.service.toyhelloworld.application.port.out.SaveDoubleLedgerEntryPort
import org.service.toyhelloworld.common.PersistentAdapter
import org.service.toyhelloworld.domain.ledger.DoubleLedgerEntry
import org.service.toyhelloworld.domain.payment.PaymentEventMessage

@PersistentAdapter
class LedgerPersistenceAdapter(
    private val ledgerTransactionRepository: LedgerTransactionRepository,
    private val ledgerEntryRepository: JpaLedgerEntryRepository,
) :  SaveDoubleLedgerEntryPort, DuplicateLegerMessageFilterPort {
    override fun isAlreadyProcess(message: PaymentEventMessage): Boolean {
        return ledgerTransactionRepository.isExist(message)
    }

    override fun save(doubleLedgerEntries: List<DoubleLedgerEntry>) {
        return ledgerEntryRepository.save(doubleLedgerEntries)
    }
}