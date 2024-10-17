package org.service.toyhelloworld.application.service

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.service.toyhelloworld.adapter.out.persistent.entity.JpaWalletEntity
import org.service.toyhelloworld.adapter.out.persistent.repository.SpringDataJpaWalletRepository
import org.service.toyhelloworld.adapter.out.persistent.repository.SpringDataJpaWalletTransactionRepository
import org.service.toyhelloworld.application.port.out.DuplicateWalletMessageFilterPort
import org.service.toyhelloworld.application.port.out.LoadPaymentOrderPort
import org.service.toyhelloworld.application.port.out.LoadWalletPort
import org.service.toyhelloworld.application.port.out.SaveWalletPort
import org.service.toyhelloworld.domain.payment.PaymentEventMessage
import org.service.toyhelloworld.domain.payment.PaymentEventMessageType
import org.service.toyhelloworld.domain.payment.PaymentOrder
import org.service.toyhelloworld.domain.wallet.WalletEventMessageType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*

@SpringBootTest
@ActiveProfiles("local")
class SettlementServiceTest(
    @Autowired private val duplicateWalletMessageFilterPort: DuplicateWalletMessageFilterPort,
    @Autowired private val loadWalletPort: LoadWalletPort,
    @Autowired private val saveWallerPort: SaveWalletPort,
    @Autowired private val springDataJpaWalletRepository: SpringDataJpaWalletRepository,
    @Autowired private val springDataJpaWalletTransactionRepository: SpringDataJpaWalletTransactionRepository,
) {

    @BeforeEach
    fun clean() {
        springDataJpaWalletTransactionRepository.deleteAll()
        springDataJpaWalletRepository.deleteAll()
    }

    @Test
    fun `should process settlement successfully`() {
        val jpaWalletEntities = listOf(
            JpaWalletEntity(
                userId = 1L,
                balance = BigDecimal.ZERO,
                version = 0
            ),
            JpaWalletEntity(
                userId = 2L,
                balance = BigDecimal.ZERO,
                version = 0
            )
        )

        springDataJpaWalletRepository.saveAll(jpaWalletEntities)

        val paymentEventMessage = PaymentEventMessage(
            type = PaymentEventMessageType.PAYMENT_CONFIRMATION_SUCCESS,
            payload = mapOf(
                "orderId" to UUID.randomUUID().toString(),
            )
        )

        val loadPaymentOrderPort = mockk<LoadPaymentOrderPort>()

        every { loadPaymentOrderPort.getPaymentOrders(paymentEventMessage.orderId()) } returns listOf(
            PaymentOrder(
                id = 1,
                sellerId = 1,
                amount = 3000L,
                orderId = paymentEventMessage.orderId()
            ),
            PaymentOrder(
                id = 2,
                sellerId = 2,
                amount = 4000L,
                orderId = paymentEventMessage.orderId()
            )
        )

        val settlementService = SettlementService(
            duplicateWalletMessageFilterPort = duplicateWalletMessageFilterPort,
            loadWalletPort = loadWalletPort,
            loadPaymentOrderPort = loadPaymentOrderPort,
            saveWalletPort = saveWallerPort
        )

        val walletEventMessage = settlementService.processSettlement(paymentEventMessage)

        val updatedWallets =
            loadWalletPort.getWallets(jpaWalletEntities.map { it.userId }.toSet()).sortedBy { it.userId }

        assertThat(walletEventMessage.payload["orderId"]).isEqualTo(paymentEventMessage.orderId())
        assertThat(walletEventMessage.type).isEqualTo(WalletEventMessageType.SUCCESS)
        assertThat(updatedWallets[0].balance.toInt()).isEqualTo(3000)
        assertThat(updatedWallets[1].balance.toInt()).isEqualTo(4000)
    }

    @Test
    fun `should be processed only once if it is called multiple times`() {
        val jpaWalletEntities = listOf(
            JpaWalletEntity(
                userId = 1L,
                balance = BigDecimal.ZERO,
                version = 0
            ),
            JpaWalletEntity(
                userId = 2L,
                balance = BigDecimal.ZERO,
                version = 0
            )
        )

        springDataJpaWalletRepository.saveAll(jpaWalletEntities)

        val paymentEventMessage = PaymentEventMessage(
            type = PaymentEventMessageType.PAYMENT_CONFIRMATION_SUCCESS,
            payload = mapOf(
                "orderId" to UUID.randomUUID().toString(),
            )
        )

        val loadPaymentOrderPort = mockk<LoadPaymentOrderPort>()

        every { loadPaymentOrderPort.getPaymentOrders(paymentEventMessage.orderId()) } returns listOf(
            PaymentOrder(
                id = 1,
                sellerId = 1,
                amount = 3000L,
                orderId = paymentEventMessage.orderId()
            ),
            PaymentOrder(
                id = 2,
                sellerId = 2,
                amount = 4000L,
                orderId = paymentEventMessage.orderId()
            )
        )

        val settlementService = SettlementService(
            duplicateWalletMessageFilterPort = duplicateWalletMessageFilterPort,
            loadWalletPort = loadWalletPort,
            loadPaymentOrderPort = loadPaymentOrderPort,
            saveWalletPort = saveWallerPort
        )

        settlementService.processSettlement(paymentEventMessage)
        settlementService.processSettlement(paymentEventMessage)

        val updatedWallets =
            loadWalletPort.getWallets(jpaWalletEntities.map { it.userId }.toSet()).sortedBy { it.userId }

        assertThat(updatedWallets[0].balance.toInt()).isEqualTo(3000)
        assertThat(updatedWallets[1].balance.toInt()).isEqualTo(4000)
    }

}