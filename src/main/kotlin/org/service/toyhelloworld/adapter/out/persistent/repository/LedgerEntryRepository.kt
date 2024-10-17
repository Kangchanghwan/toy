package org.service.toyhelloworld.adapter.out.persistent.repository

import org.service.toyhelloworld.domain.ledger.DoubleLedgerEntry


interface LedgerEntryRepository {
    fun save(doubleLedgerEntries: List<DoubleLedgerEntry>)
}