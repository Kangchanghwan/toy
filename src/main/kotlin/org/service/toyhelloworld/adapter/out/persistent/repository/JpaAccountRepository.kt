package org.service.toyhelloworld.adapter.out.persistent.repository

import org.service.toyhelloworld.adapter.out.persistent.entity.JpaAccountEntity
import org.service.toyhelloworld.adapter.out.persistent.entity.JpaAccountMapper
import org.service.toyhelloworld.domain.ledger.DoubleAccountsForLedger
import org.service.toyhelloworld.domain.ledger.FinanceType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
class JpaAccountRepository(
    private val springDataJpaAccountRepository: SpringDataJpaAccountRepository,
    private val jpaAccountMapper: JpaAccountMapper,
) : AccountRepository {
    override fun getDoubleAccountForLedger(financeType: FinanceType): DoubleAccountsForLedger {
        return when (financeType) {
            FinanceType.PAYMENT_ORDER -> {
                val to = springDataJpaAccountRepository.findByName(REVENUE_ACCOUNT_NAME)
                val from = springDataJpaAccountRepository.findByName(ITEM_BUYER_ACCOUNT_NAME)

                DoubleAccountsForLedger(
                    to = jpaAccountMapper.mapToDomainEntity(to),
                    from = jpaAccountMapper.mapToDomainEntity(from)
                )
            }
        }
    }

    companion object {
        const val REVENUE_ACCOUNT_NAME = "REVENUE"
        const val ITEM_BUYER_ACCOUNT_NAME = "ITEM_BUYER"
    }
}

interface SpringDataJpaAccountRepository : JpaRepository<JpaAccountEntity, Long> {
    fun findByName(name: String): JpaAccountEntity
}