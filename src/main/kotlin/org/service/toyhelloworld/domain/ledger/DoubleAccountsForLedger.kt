package org.service.toyhelloworld.domain.ledger

import org.service.toyhelloworld.domain.ledger.Account

data class DoubleAccountsForLedger(
    val to: Account,
    val from: Account
)