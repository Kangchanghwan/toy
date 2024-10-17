package org.service.toyhelloworld.application.port.out

import org.service.toyhelloworld.domain.wallet.Wallet


interface LoadWalletPort {
    fun getWallets(sellerIds: Set<Long>): Set<Wallet>
}