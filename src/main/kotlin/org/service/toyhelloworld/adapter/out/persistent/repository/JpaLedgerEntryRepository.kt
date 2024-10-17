package org.service.toyhelloworld.adapter.out.persistent.repository

import jakarta.transaction.Transactional
import org.service.toyhelloworld.adapter.out.persistent.entity.JpaLedgerEntryEntity
import org.service.toyhelloworld.adapter.out.persistent.entity.JpaLedgerEntryMapper
import org.service.toyhelloworld.domain.ledger.DoubleLedgerEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
class JpaLedgerEntryRepository(
    private val springDataJpaLedgerEntryRepository: SpringDataJpaLedgerEntryRepository,
    private val jpaLedgerEntryMapper: JpaLedgerEntryMapper,
) : LedgerEntryRepository {
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    override fun save(doubleLedgerEntries: List<DoubleLedgerEntry>) {
        springDataJpaLedgerEntryRepository.saveAll(
            doubleLedgerEntries.flatMap { jpaLedgerEntryMapper.mapToEntity(it) }
        )
    }
}

interface SpringDataJpaLedgerEntryRepository : JpaRepository<JpaLedgerEntryEntity, Long> {

}