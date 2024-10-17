package org.service.toyhelloworld.adapter.`in`.event.internal

import org.service.toyhelloworld.application.service.PaymentCompleteService
import org.service.toyhelloworld.common.EventAdapter
import org.service.toyhelloworld.domain.ledger.LedgerEventMessage
import org.service.toyhelloworld.domain.wallet.WalletEventMessage
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener

@Configuration
@EventAdapter
class PaymentServiceCompleteEventMessageHandler(
    private val paymentCompleteService: PaymentCompleteService
) {

    @EventListener
    fun walletEventMessageHandler(walletEventMessage: WalletEventMessage){
        paymentCompleteService.completePayment(walletEventMessage)
    }
    @EventListener
    fun ledgerEventMessageHandler(legerEventMessage: LedgerEventMessage){
        paymentCompleteService.completePayment(legerEventMessage)
    }


}