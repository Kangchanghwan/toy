package org.service.toyhelloworld.adapter.out.persistent

import org.service.toyhelloworld.adapter.out.persistent.repository.WalletRepository
import org.service.toyhelloworld.adapter.out.persistent.repository.WalletTransactionRepository
import org.service.toyhelloworld.application.port.out.DuplicateMessageFilterPort
import org.service.toyhelloworld.application.port.out.LoadWalletPort
import org.service.toyhelloworld.application.port.out.SaveWalletPort
import org.service.toyhelloworld.common.PersistentAdapter
import org.service.toyhelloworld.domain.payment.PaymentEventMessage
import org.service.toyhelloworld.domain.wallet.Wallet

@PersistentAdapter
class WalletPersistenceAdapter(
    private val walletTransactionRepository: WalletTransactionRepository,
    private val walletRepository: WalletRepository,
) : DuplicateMessageFilterPort, LoadWalletPort, SaveWalletPort {
    override fun isAlreadyProcess(paymentEventMessage: PaymentEventMessage): Boolean {
        return walletTransactionRepository.isExist(paymentEventMessage)
    }
    override fun getWallets(sellerIds: Set<Long>): Set<Wallet> {
        return walletRepository.getWallets(sellerIds)
    }
    override fun save(wallets: List<Wallet>) {
        walletRepository.save(wallets)
    }
}