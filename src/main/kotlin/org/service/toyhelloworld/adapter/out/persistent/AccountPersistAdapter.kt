package org.service.toyhelloworld.adapter.out.persistent

import org.service.toyhelloworld.adapter.out.persistent.repository.JpaAccountRepository
import org.service.toyhelloworld.application.port.out.LoadAccountPort
import org.service.toyhelloworld.common.PersistentAdapter
import org.service.toyhelloworld.domain.ledger.DoubleAccountsForLedger
import org.service.toyhelloworld.domain.ledger.FinanceType

@PersistentAdapter
class AccountPersistAdapter(
    private val accountRepository: JpaAccountRepository
) : LoadAccountPort {
    override fun getDoubleAccountsForLedger(financeType: FinanceType): DoubleAccountsForLedger {
        return accountRepository.getDoubleAccountForLedger(financeType)
    }
}