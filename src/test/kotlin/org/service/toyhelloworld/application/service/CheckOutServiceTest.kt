package org.service.toyhelloworld.application.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.service.toyhelloworld.adapter.test.PaymentDatabaseHelper
import org.service.toyhelloworld.adapter.test.PaymentTestConfiguration
import org.service.toyhelloworld.application.port.`in`.CheckOutCommand
import org.service.toyhelloworld.application.port.`in`.CheckOutUseCase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ActiveProfiles
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.assertFalse

@SpringBootTest
@Import(PaymentTestConfiguration::class)
@ActiveProfiles("local")
class CheckOutServiceTest(
    @Autowired private val checkOutUseCase: CheckOutUseCase,
    @Autowired private val paymentDatabaseHelper: PaymentDatabaseHelper
) {

    @BeforeTest
    fun setUp() {
        paymentDatabaseHelper.clean()
    }

    @Test
    fun `should save PaymentEvent and PaymentOrder successfully`() {
        val orderId = UUID.randomUUID().toString()
        val checkOutCommand = CheckOutCommand(
            cartId = 1, buyerId = 1, productIds = listOf(1, 2, 3), idempotencyKey = orderId
        )
        checkOutUseCase.checkOut(checkOutCommand)

        val paymentEvent = paymentDatabaseHelper.getPayments(orderId)!!

        assertThat(paymentEvent.orderId).isEqualTo(orderId)
        assertThat(paymentEvent.totalAmount()).isEqualTo(60_000)
        assertThat(paymentEvent.paymentOrders.size).isEqualTo(checkOutCommand.productIds.size)
        assertFalse(paymentEvent.isPaymentDone())
        assertTrue(paymentEvent.paymentOrders.all { !it.isLegerUpdated() })
        assertTrue(paymentEvent.paymentOrders.all { !it.isLegerUpdated() })
    }

    @Test
    fun `should fail to save PaymentEvent and PaymentOrder when trying to save for the second time`() {
        val orderId = UUID.randomUUID().toString()
        val checkOutCommand = CheckOutCommand(
            cartId = 1, buyerId = 1, productIds = listOf(1, 2, 3), idempotencyKey = orderId
        )

        checkOutUseCase.checkOut(checkOutCommand)

        assertThrows<DataIntegrityViolationException> {
            checkOutUseCase.checkOut(checkOutCommand)
        }
    }
}