package org.service.toyhelloworld.adapter.`in`.event.internal

import org.service.toyhelloworld.common.EventAdapter
import org.service.toyhelloworld.domain.payment.PaymentEventMessage
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Configuration
@EventAdapter
class LedgerServicePaymentEventMessageHandler {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun consume(paymentEventMessage: PaymentEventMessage){
        println(paymentEventMessage)
    }

}