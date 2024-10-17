package org.service.toyhelloworld.application.service

import org.service.toyhelloworld.application.port.`in`.PaymentCompleteUseCase
import org.service.toyhelloworld.application.port.out.LoadPaymentPort
import org.service.toyhelloworld.application.port.out.PaymentCompletePort
import org.service.toyhelloworld.common.UseCase
import org.service.toyhelloworld.domain.ledger.LedgerEventMessage
import org.service.toyhelloworld.domain.wallet.WalletEventMessage

@UseCase
class PaymentCompleteService(
    private val loadPaymentPort: LoadPaymentPort,
    private val paymentCompletePort: PaymentCompletePort,
) : PaymentCompleteUseCase {

    override fun completePayment(walletEventMessage: WalletEventMessage){
        loadPaymentPort.getPayment(walletEventMessage.orderId())
            .apply {
                confirmWalletUpdate()
                completeIfDone() }
            .let { paymentCompletePort.complete(it) }
    }

    override fun completePayment(ledgerEventMessage: LedgerEventMessage) {
        loadPaymentPort.getPayment(ledgerEventMessage.orderId())
            .apply {
                confirmWalletUpdate()
                completeIfDone() }
            .let { paymentCompletePort.complete(it) }
    }

}