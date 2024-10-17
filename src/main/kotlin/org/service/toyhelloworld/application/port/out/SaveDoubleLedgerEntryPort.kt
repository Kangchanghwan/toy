package org.service.toyhelloworld.application.port.out

import org.service.toyhelloworld.domain.ledger.DoubleLedgerEntry

interface SaveDoubleLedgerEntryPort {
    fun save(doubleLedgerEntries: List<DoubleLedgerEntry>)
}