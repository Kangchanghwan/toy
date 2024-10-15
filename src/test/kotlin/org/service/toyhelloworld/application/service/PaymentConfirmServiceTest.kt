package org.service.toyhelloworld.application.service

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.service.toyhelloworld.adapter.`in`.web.toss.exception.PSPConfirmationException
import org.service.toyhelloworld.adapter.`in`.web.toss.exception.TossPaymentError
import org.service.toyhelloworld.adapter.out.persistent.exception.PaymentValidationException
import org.service.toyhelloworld.adapter.test.PaymentDatabaseHelper
import org.service.toyhelloworld.adapter.test.PaymentTestConfiguration
import org.service.toyhelloworld.application.port.`in`.CheckOutCommand
import org.service.toyhelloworld.application.port.`in`.CheckOutUseCase
import org.service.toyhelloworld.application.port.`in`.PaymentConfirmCommand
import org.service.toyhelloworld.application.port.out.PaymentExecutionPort
import org.service.toyhelloworld.application.port.out.PaymentStatusUpdatePort
import org.service.toyhelloworld.application.port.out.PaymentValidationPort
import org.service.toyhelloworld.domain.payment.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.test.BeforeTest

@SpringBootTest
@Import(PaymentTestConfiguration::class)
@ActiveProfiles("local")
class PaymentConfirmServiceTest(
    @Autowired private val paymentDatabaseHelper: PaymentDatabaseHelper,
    @Autowired private val checkOutUseCase: CheckOutUseCase,
    @Autowired private val paymentStatusUpdatePort: PaymentStatusUpdatePort,
    @Autowired private val paymentValidationPort: PaymentValidationPort,
    @Autowired private val paymentErrorHandler: PaymentErrorHandler,
){
    private val mockPaymentExecutorPort = mockk<PaymentExecutionPort>()

    @BeforeTest
    fun setUp() {
        paymentDatabaseHelper.clean()
    }

    @Test
    fun `should be marked as SUCCESS if Payment Confirmation success in PSP`() {
        val orderId = UUID.randomUUID().toString()

        val checkOutCommand = CheckOutCommand(
            cartId = 1,
            buyerId = 1,
            productIds = listOf(1, 2, 3),
            idempotencyKey = orderId
        )

        val checkOutResult = checkOutUseCase.checkOut(checkOutCommand)

        val paymentConfirmCommand = PaymentConfirmCommand(
            paymentKey = UUID.randomUUID().toString(),
            orderId = checkOutResult.orderId,
            amount = checkOutResult.amount
        )

        val paymentConfirmService = PaymentConfirmService(
            paymentStatusUpdatePort = paymentStatusUpdatePort,
            paymentValidationPort = paymentValidationPort,
            paymentExecutionPort = mockPaymentExecutorPort,
            paymentErrorHandler = paymentErrorHandler
        )

        val paymentExecutionResult = PaymentExecutionResult(
            paymentKey = paymentConfirmCommand.paymentKey,
            orderId = paymentConfirmCommand.orderId,
            extraDetails = PaymentExtraDetails(
                type = PaymentType.NORMAL,
                method = PaymentMethod.CARD,
                totalAmount = paymentConfirmCommand.amount,
                orderName = "test_order_name",
                pspConfirmationStatus = PSPConfirmationsStatus.DONE,
                approvedAt = LocalDateTime.now(),
                pspRawData = "{}"
            ),
            isSuccess = true,
            isUnknown = false,
            isFailure = false,
            isRetryable = false
        )

        every { mockPaymentExecutorPort.execute(paymentConfirmCommand) } returns paymentExecutionResult
        val paymentConfirmResult = paymentConfirmService.confirm(paymentConfirmCommand)

        val paymentEvent = paymentDatabaseHelper.getPayments(orderId)!!

        assertThat(paymentConfirmResult.status).isEqualTo(PaymentStatus.SUCCESS)
        assertTrue(paymentEvent.isSuccess())
        assertThat(paymentEvent.paymentType).isEqualTo(paymentExecutionResult.extraDetails!!.type)
        assertThat(paymentEvent.paymentMethod).isEqualTo(paymentExecutionResult.extraDetails!!.method)
        assertThat(paymentEvent.orderName).isEqualTo(paymentExecutionResult.extraDetails!!.orderName)
        assertThat(paymentEvent.approvedAt?.truncatedTo(ChronoUnit.MINUTES)).isEqualTo(paymentExecutionResult.extraDetails!!.approvedAt.truncatedTo(
            ChronoUnit.MINUTES))
    }
    @Test
    fun `should be marked as SUCCESS if Payment Confirmation fail on PSP`() {
        val orderId = UUID.randomUUID().toString()

        val checkOutCommand = CheckOutCommand(
            cartId = 1,
            buyerId = 1,
            productIds = listOf(1,2,3),
            idempotencyKey = orderId
        )

        val checkOutResult = checkOutUseCase.checkOut(checkOutCommand)

        val paymentConfirmCommand = PaymentConfirmCommand(
            paymentKey = UUID.randomUUID().toString(),
            orderId = checkOutResult.orderId,
            amount = checkOutResult.amount
        )

        val paymentConfirmService = PaymentConfirmService(
            paymentStatusUpdatePort = paymentStatusUpdatePort,
            paymentValidationPort = paymentValidationPort,
            paymentExecutionPort = mockPaymentExecutorPort,
            paymentErrorHandler = paymentErrorHandler
        )

        val paymentExecutionResult = PaymentExecutionResult(
            paymentKey = paymentConfirmCommand.paymentKey,
            orderId = paymentConfirmCommand.orderId,
            failure = PaymentFailure(
                errorCode = "ERROR",
                message = "TEST Error Message"
            ) ,
            isSuccess = false,
            isUnknown = false,
            isFailure = true,
            isRetryable = false
        )

        every { mockPaymentExecutorPort.execute(paymentConfirmCommand) } returns paymentExecutionResult
        val paymentConfirmResult = paymentConfirmService.confirm(paymentConfirmCommand)

        val paymentEvent = paymentDatabaseHelper.getPayments(orderId)!!

        assertThat(paymentConfirmResult.status).isEqualTo(PaymentStatus.FAILURE)
        assertTrue(paymentEvent.isFailure())
    }
    @Test
    fun `should be marked as SUCCESS if Payment confirmation fails due to an unknown exception`() {
        val orderId = UUID.randomUUID().toString()

        val checkOutCommand = CheckOutCommand(
            cartId = 1,
            buyerId = 1,
            productIds = listOf(1,2,3),
            idempotencyKey = orderId
        )

        val checkOutResult = checkOutUseCase.checkOut(checkOutCommand)

        val paymentConfirmCommand = PaymentConfirmCommand(
            paymentKey = UUID.randomUUID().toString(),
            orderId = checkOutResult.orderId,
            amount = checkOutResult.amount
        )

        val paymentConfirmService = PaymentConfirmService(
            paymentStatusUpdatePort = paymentStatusUpdatePort,
            paymentValidationPort = paymentValidationPort,
            paymentExecutionPort = mockPaymentExecutorPort,
            paymentErrorHandler = paymentErrorHandler
        )

        val paymentExecutionResult = PaymentExecutionResult(
            paymentKey = paymentConfirmCommand.paymentKey,
            orderId = paymentConfirmCommand.orderId,
            failure = PaymentFailure(
                errorCode = "ERROR",
                message = "TEST Error Message"
            ) ,
            isSuccess = false,
            isUnknown = true,
            isFailure = false,
            isRetryable = false
        )

        every { mockPaymentExecutorPort.execute(paymentConfirmCommand) } returns paymentExecutionResult
        val paymentConfirmResult = paymentConfirmService.confirm(paymentConfirmCommand)

        val paymentEvent = paymentDatabaseHelper.getPayments(orderId)!!

        assertThat(paymentConfirmResult.status).isEqualTo(PaymentStatus.UNKNOWN)
        assertTrue(paymentEvent.isUnknown())
    }
    @Test
    fun `should handle PSPConfirmationException`(){
        val orderId = UUID.randomUUID().toString()

        val checkOutCommand = CheckOutCommand(
            cartId = 1,
            buyerId = 1,
            productIds = listOf(1,2,3),
            idempotencyKey = orderId
        )

        val checkOutResult = checkOutUseCase.checkOut(checkOutCommand)

        val paymentConfirmCommand = PaymentConfirmCommand(
            paymentKey = UUID.randomUUID().toString(),
            orderId = checkOutResult.orderId,
            amount = checkOutResult.amount
        )

        val paymentConfirmService = PaymentConfirmService(
            paymentStatusUpdatePort = paymentStatusUpdatePort,
            paymentValidationPort = paymentValidationPort,
            paymentExecutionPort = mockPaymentExecutorPort,
            paymentErrorHandler = paymentErrorHandler
        )

        val pspConfirmationException = PSPConfirmationException(
            errorCode = TossPaymentError.REJECT_CARD_PAYMENT.name,
            errorMessage = TossPaymentError.REJECT_CARD_PAYMENT.description,
            isSuccess = false,
            isFailure = true,
            isUnknown = false,
            isRetryableError = false
        )

        every { mockPaymentExecutorPort.execute(paymentConfirmCommand) } throws pspConfirmationException

        val paymentConfirmResult = paymentConfirmService.confirm(paymentConfirmCommand)
        val paymentEvent = paymentDatabaseHelper.getPayments(orderId)!!
        assertThat(paymentConfirmResult.status).isEqualTo(PaymentStatus.FAILURE)
        assertTrue(paymentEvent.isFailure())
    }
    @Test
    fun `should handle PaymentValidationException`(){
        val orderId = UUID.randomUUID().toString()

        val checkOutCommand = CheckOutCommand(
            cartId = 1,
            buyerId = 1,
            productIds = listOf(1,2,3),
            idempotencyKey = orderId
        )

        val checkOutResult = checkOutUseCase.checkOut(checkOutCommand)

        val paymentConfirmCommand = PaymentConfirmCommand(
            paymentKey = UUID.randomUUID().toString(),
            orderId = checkOutResult.orderId,
            amount = checkOutResult.amount
        )
        val mockPaymentValidationPort = mockk<PaymentValidationPort>()

        val paymentConfirmService = PaymentConfirmService(
            paymentStatusUpdatePort = paymentStatusUpdatePort,
            paymentValidationPort = mockPaymentValidationPort,
            paymentExecutionPort = mockPaymentExecutorPort,
            paymentErrorHandler = paymentErrorHandler
        )

        every { mockPaymentValidationPort.isValid(orderId, paymentConfirmCommand.amount) } throws PaymentValidationException("결재 유효성 검증에 실패하였습니다.")

        val paymentConfirmResult = paymentConfirmService.confirm(paymentConfirmCommand)
        val paymentEvent = paymentDatabaseHelper.getPayments(orderId)!!
        assertThat(paymentConfirmResult.status).isEqualTo(PaymentStatus.FAILURE)
        assertTrue(paymentEvent.isFailure())
    }

    @Test
    @Tag("ExternalIntegration")
    fun `should send the event message to the external message system after the payment confirmation has been successful`(){
        val orderId = UUID.randomUUID().toString()

        val checkOutCommand = CheckOutCommand(
            cartId = 1,
            buyerId = 1,
            productIds = listOf(1,2,3),
            idempotencyKey = orderId
        )

        val checkOutResult = checkOutUseCase.checkOut(checkOutCommand)

        val paymentConfirmCommand = PaymentConfirmCommand(
            paymentKey = UUID.randomUUID().toString(),
            orderId = checkOutResult.orderId,
            amount = checkOutResult.amount
        )

        val paymentConfirmService = PaymentConfirmService(
            paymentStatusUpdatePort = paymentStatusUpdatePort,
            paymentValidationPort = paymentValidationPort,
            paymentExecutionPort = mockPaymentExecutorPort,
            paymentErrorHandler = paymentErrorHandler
        )

        val paymentExecutionResult = PaymentExecutionResult(
            paymentKey = paymentConfirmCommand.paymentKey,
            orderId = paymentConfirmCommand.orderId,
            extraDetails = PaymentExtraDetails(
                type = PaymentType.NORMAL,
                method = PaymentMethod.CARD,
                totalAmount = paymentConfirmCommand.amount,
                orderName = "test_order_name",
                pspConfirmationStatus = PSPConfirmationsStatus.DONE,
                approvedAt = LocalDateTime.now(),
                pspRawData = "{}"
            ),
            isSuccess = true,
            isUnknown = false,
            isFailure = false,
            isRetryable = false
        )

        every { mockPaymentExecutorPort.execute(paymentConfirmCommand) } returns paymentExecutionResult
        paymentConfirmService.confirm(paymentConfirmCommand)
    }
}