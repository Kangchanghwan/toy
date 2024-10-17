package org.service.toyhelloworld.adapter.out.persistent.entity

import jakarta.persistence.*
import org.service.toyhelloworld.domain.ledger.LedgerEntryType
import java.math.BigDecimal

@Entity
@Table(name = "ledger_entries")
class JpaLedgerEntryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null,

    val amount: BigDecimal,

    @Column(name = "account_id")
    val accountId: Long,

    @ManyToOne(cascade = [CascadeType.PERSIST])
    val transaction: JpaLedgerTransactionEntity,

    @Enumerated(EnumType.STRING)
    val type: LedgerEntryType
)
