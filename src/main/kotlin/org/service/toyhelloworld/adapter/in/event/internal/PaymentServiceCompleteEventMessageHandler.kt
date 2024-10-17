package org.service.toyhelloworld.adapter.`in`.event.internal

import org.service.toyhelloworld.common.EventAdapter
import org.service.toyhelloworld.domain.wallet.WalletEventMessage
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener

@Configuration
@EventAdapter
class PaymentServiceCompleteEventMessageHandler {

    @EventListener
    fun walletEventMessageHandler(walletEventMessage: WalletEventMessage){
        println(walletEventMessage)
    }

}