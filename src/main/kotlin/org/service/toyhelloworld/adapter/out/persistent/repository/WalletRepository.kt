package org.service.toyhelloworld.adapter.out.persistent.repository

import org.service.toyhelloworld.domain.wallet.Wallet

interface WalletRepository {
    fun getWallets(sellerIds: Set<Long>): Set<Wallet>
    fun save(wallets: List<Wallet>)
}