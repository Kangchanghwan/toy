package org.service.toyhelloworld.application.port.`in`

import org.service.toyhelloworld.domain.payment.PaymentEventMessage
import org.service.toyhelloworld.domain.wallet.WalletEventMessage


interface SettlementUseCase {
    fun processSettlement(paymentEventMessage: PaymentEventMessage): WalletEventMessage
}