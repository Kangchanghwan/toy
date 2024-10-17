package org.service.toyhelloworld.application.port.out

import org.service.toyhelloworld.domain.ledger.DoubleAccountsForLedger
import org.service.toyhelloworld.domain.ledger.FinanceType

interface LoadAccountPort {
 fun getDoubleAccountsForLedger(financeType: FinanceType): DoubleAccountsForLedger
}