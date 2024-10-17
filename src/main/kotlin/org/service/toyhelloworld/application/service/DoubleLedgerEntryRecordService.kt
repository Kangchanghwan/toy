package org.service.toyhelloworld.application.service

import org.service.toyhelloworld.application.port.`in`.DoubleLedgerEntryRecordUseCase
import org.service.toyhelloworld.application.port.out.DuplicateWalletMessageFilterPort
import org.service.toyhelloworld.application.port.out.LoadAccountPort
import org.service.toyhelloworld.application.port.out.LoadPaymentOrderPort
import org.service.toyhelloworld.application.port.out.SaveDoubleLedgerEntryPort
import org.service.toyhelloworld.common.UseCase
import org.service.toyhelloworld.domain.ledger.*
import org.service.toyhelloworld.domain.payment.PaymentEventMessage


@UseCase
class DoubleLedgerEntryRecordService(
    private val duplicateWalletMessageFilterPort: DuplicateWalletMessageFilterPort,
    private val loadAccountPort: LoadAccountPort,
    private val loadPaymentOrderPort: LoadPaymentOrderPort,
    private val saveDoubleLedgerEntryPort: SaveDoubleLedgerEntryPort
) : DoubleLedgerEntryRecordUseCase {
    override fun recordDoubleLegerEntry(message: PaymentEventMessage): LedgerEventMessage {
        if(duplicateWalletMessageFilterPort.isAlreadyProcess(message)){
            return createLedgerEventMessage(message)
        }

        val doubleAccounts = loadAccountPort.getDoubleAccountsForLedger(FinanceType.PAYMENT_ORDER)
        val items = loadPaymentOrderPort.getPaymentOrders(message.orderId()).map {
            Item(
                id = it.id!!,
                amount = it.amount,
                orderId = it.orderId,
                type = ReferenceType.PAYMENT_ORDER
            )
        }

        val doubleLedgerEntries = Ledger.createDoubleLedgerEntry(doubleAccounts, items)

        saveDoubleLedgerEntryPort.save(doubleLedgerEntries)

        return createLedgerEventMessage(message)
    }

    private fun createLedgerEventMessage(message: PaymentEventMessage) =
        LedgerEventMessage(
            type = LedgerEventMessageType.SUCCESS,
            payload = mapOf("orderId" to message.orderId()),
            metadata = message.metadata
        )
}
