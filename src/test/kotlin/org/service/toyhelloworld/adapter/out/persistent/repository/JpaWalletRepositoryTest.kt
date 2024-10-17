package org.service.toyhelloworld.adapter.out.persistent.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import org.service.toyhelloworld.adapter.out.persistent.entity.JpaWalletEntity
import org.service.toyhelloworld.adapter.out.persistent.entity.JpaWalletMapper
import org.service.toyhelloworld.domain.wallet.Item
import org.service.toyhelloworld.domain.wallet.ReferenceType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.Executors

@SpringBootTest
@ActiveProfiles("local")
class JpaWalletRepositoryTest(
    @Autowired private val walletRepository: WalletRepository,
    @Autowired private val springDataJpaWalletRepository: SpringDataJpaWalletRepository,
    @Autowired private val springDataJpaWalletTransactionRepository: SpringDataJpaWalletTransactionRepository,
    @Autowired private val jpaWalletMapper: JpaWalletMapper,
) {

    @BeforeEach
    fun clean() {
        springDataJpaWalletRepository.deleteAll()
        springDataJpaWalletTransactionRepository.deleteAll()
    }

    @RepeatedTest(value = 5)
    fun `should update the balance of wallet successfully when execute the updated command at the same time`() {
        val jpaWalletEntity1 = JpaWalletEntity(
            userId = 1L,
            balance = BigDecimal.ZERO,
            version = 0
        )
        val jpaWalletEntity2 = JpaWalletEntity(
            userId = 2L,
            balance = BigDecimal.ZERO,
            version = 0
        )

        springDataJpaWalletRepository.saveAll(
            listOf
                (jpaWalletEntity1, jpaWalletEntity2)
        )

        val baseWallet1 = jpaWalletMapper.mapToDomainEntity(jpaWalletEntity1)
        val baseWallet2 = jpaWalletMapper.mapToDomainEntity(jpaWalletEntity2)

        val items1 = listOf(
            Item(
                amount = 1000L,
                orderId = UUID.randomUUID().toString(),
                referenceId = 1L,
                referenceType = ReferenceType.PAYMENT_ORDER
            )
        )
        val items2 = listOf(
            Item(
                amount = 2000L,
                orderId = UUID.randomUUID().toString(),
                referenceId = 2L,
                referenceType = ReferenceType.PAYMENT_ORDER
            )
        )
        val items3 = listOf(
            Item(
                amount = 3000L,
                orderId = UUID.randomUUID().toString(),
                referenceId = 3L,
                referenceType = ReferenceType.PAYMENT_ORDER
            )
        )


        val updatedWallet1 = baseWallet1.calculateBalanceWith(items1)
        val updatedWallet2 = baseWallet1.calculateBalanceWith(items2)
        val updatedWallet3 = baseWallet1.calculateBalanceWith(items3)

        val updatedWallet4 = baseWallet2.calculateBalanceWith(items1)
        val updatedWallet5 = baseWallet2.calculateBalanceWith(items2)
        val updatedWallet6 = baseWallet2.calculateBalanceWith(items3)

        val executorService = Executors.newFixedThreadPool(3)

        val future1 = executorService.submit { walletRepository.save(listOf(updatedWallet1, updatedWallet4)) }
        val future2 = executorService.submit { walletRepository.save(listOf(updatedWallet2, updatedWallet5)) }
        val future3 = executorService.submit { walletRepository.save(listOf(updatedWallet3, updatedWallet6)) }

        future1.get()
        future2.get()
        future3.get()

        val retrievedWallet1 = springDataJpaWalletRepository.findById(baseWallet1.id).get()
        val retrievedWallet2 = springDataJpaWalletRepository.findById(baseWallet2.id).get()

        assertThat(retrievedWallet1.version).isEqualTo(3)
        assertThat(retrievedWallet2.version).isEqualTo(3)

        assertThat(retrievedWallet1.balance.toInt()).isEqualTo(6000)
        assertThat(retrievedWallet2.balance.toInt()).isEqualTo(6000)
    }

}