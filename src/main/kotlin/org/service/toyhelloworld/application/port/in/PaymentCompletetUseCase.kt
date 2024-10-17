package org.service.toyhelloworld.application.port.`in`

import org.service.toyhelloworld.domain.ledger.LedgerEventMessage
import org.service.toyhelloworld.domain.wallet.WalletEventMessage


interface PaymentCompleteUseCase {
    fun completePayment(walletEventMessage: WalletEventMessage)
    fun completePayment(ledgerEventMessage: LedgerEventMessage)
}