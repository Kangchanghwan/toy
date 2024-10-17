package org.service.toyhelloworld.adapter.out.persistent.entity

import org.service.toyhelloworld.domain.wallet.Wallet
import org.springframework.stereotype.Component

@Component
class JpaWalletMapper {

    fun mapToDomainEntity(jpaWalletEntity: JpaWalletEntity): Wallet {
        return Wallet(
            id = jpaWalletEntity.id!!,
            userId = jpaWalletEntity.userId,
            version = jpaWalletEntity.version,
            balance = jpaWalletEntity.balance,
        )
    }
    fun mapToJpaEntity(wallet: Wallet): JpaWalletEntity {
        return JpaWalletEntity(
            id = wallet.id,
            userId = wallet.userId,
            version = wallet.version,
            balance = wallet.balance,
        )
    }

}