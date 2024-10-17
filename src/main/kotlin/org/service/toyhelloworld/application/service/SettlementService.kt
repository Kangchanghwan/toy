package org.service.toyhelloworld.application.service

import org.service.toyhelloworld.application.port.`in`.SettlementUseCase
import org.service.toyhelloworld.application.port.out.DuplicateMessageFilterPort
import org.service.toyhelloworld.application.port.out.LoadPaymentOrderPort
import org.service.toyhelloworld.application.port.out.LoadWalletPort
import org.service.toyhelloworld.application.port.out.SaveWalletPort
import org.service.toyhelloworld.common.UseCase
import org.service.toyhelloworld.domain.payment.PaymentEventMessage
import org.service.toyhelloworld.domain.wallet.*


@UseCase
class SettlementService(
    private val duplicateMessageFilterPort: DuplicateMessageFilterPort,
    private val loadPaymentOrderPort: LoadPaymentOrderPort,
    private val loadWalletPort: LoadWalletPort,
    private val saveWalletPort: SaveWalletPort,
) : SettlementUseCase {
    override fun processSettlement(paymentEventMessage: PaymentEventMessage): WalletEventMessage {
        if (duplicateMessageFilterPort.isAlreadyProcess(paymentEventMessage)) {
            return createWalletEventMessage(paymentEventMessage)
        }
        val paymentOrders = loadPaymentOrderPort.getPaymentOrders(paymentEventMessage.orderId())

        val paymentOrdersBySellerId = paymentOrders.groupBy(
            { it.sellerId },
            { Item(
                    amount = it.amount,
                    orderId = it.orderId,
                    referenceId = it.id!!,
                    referenceType = ReferenceType.PAYMENT_ORDER
                ) }
        )
        val updateWallets = getUpdateWallets(paymentOrdersBySellerId)

        saveWalletPort.save(updateWallets)

        return createWalletEventMessage(paymentEventMessage)
    }


    private fun createWalletEventMessage(paymentEventMessage: PaymentEventMessage) =
        WalletEventMessage(
            type = WalletEventMessageType.SUCCESS,
            payload = mapOf(
                "orderId" to paymentEventMessage.orderId()
            )
        )


    private fun getUpdateWallets(paymentOrdersBySellerId: Map<Long, List<Item>>): List<Wallet> {
        val sellerIds = paymentOrdersBySellerId.keys

        val wallets = loadWalletPort.getWallets(sellerIds)

        return wallets.map { wallet ->
            wallet.calculateBalanceWith(paymentOrdersBySellerId[wallet.userId]!!)
        }
    }
}