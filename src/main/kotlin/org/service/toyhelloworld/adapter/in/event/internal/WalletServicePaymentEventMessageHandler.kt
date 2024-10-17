package org.service.toyhelloworld.adapter.`in`.event.internal

import org.service.toyhelloworld.application.port.`in`.SettlementUseCase
import org.service.toyhelloworld.common.EventAdapter
import org.service.toyhelloworld.domain.payment.PaymentEventMessage
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Configuration
@EventAdapter
class WalletServicePaymentEventMessageHandler(
    private val settlementUseCase: SettlementUseCase,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun consume(paymentEventMessage: PaymentEventMessage){
        applicationEventPublisher.publishEvent(settlementUseCase.processSettlement(paymentEventMessage))
    }

}