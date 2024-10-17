package org.service.toyhelloworld.adapter.out.persistent.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "wallets")
data class JpaWalletEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long? = null,

    @Column(name = "user_id")
    val userId: Long,

    val balance: BigDecimal,

    @Version
    val version: Int
) {
    fun addBalance(amount: BigDecimal): JpaWalletEntity {
        return copy(
            balance = balance + amount
        )
    }
}