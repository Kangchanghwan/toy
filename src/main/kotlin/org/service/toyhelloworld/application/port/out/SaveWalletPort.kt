package org.service.toyhelloworld.application.port.out

import org.service.toyhelloworld.domain.wallet.Wallet


interface SaveWalletPort {

    fun save(wallets: List<Wallet>)
}