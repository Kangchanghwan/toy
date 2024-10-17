package org.service.toyhelloworld.adapter.out.persistent.repository

import org.service.toyhelloworld.domain.ledger.DoubleAccountsForLedger
import org.service.toyhelloworld.domain.ledger.FinanceType


interface AccountRepository {
    fun getDoubleAccountForLedger(financeType: FinanceType): DoubleAccountsForLedger
}