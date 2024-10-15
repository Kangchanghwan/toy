package org.service.toyhelloworld.adapter.`in`.web.toss.executor

import org.assertj.core.api.Assertions.assertThat
import org.service.toyhelloworld.adapter.`in`.web.toss.exception.PSPConfirmationException
import org.service.toyhelloworld.adapter.`in`.web.toss.exception.TossPaymentError
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.service.toyhelloworld.adapter.test.PSPTestWebClientConfiguration
import org.service.toyhelloworld.application.port.`in`.PaymentConfirmCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.util.*


@SpringBootTest
@Import(PSPTestWebClientConfiguration::class)
@ActiveProfiles("local")
@Tag("TooLongTime")
class TossPaymentExecutorTest(
    @Autowired private val pspTestWebClientConfiguration: PSPTestWebClientConfiguration,
) {

    @Test
    fun `should handle various TossPaymentError scenarios`() {
        generateErrorScenarios().forEach { errorScenario ->
            val command = PaymentConfirmCommand(
                paymentKey = UUID.randomUUID().toString(),
                orderId = UUID.randomUUID().toString(),
                amount = 10000L
            )

            val paymentExecutor = TossPaymentExecutor(
                tossPaymentRestClient = pspTestWebClientConfiguration.createTestTossWebClient(
                    Pair("TossPayments-Test-Code", errorScenario.errorCode)
                ),
                uri = "v1/payments/key-in",
            )

            try {
                paymentExecutor.execute(command)
            } catch (e: PSPConfirmationException) {
                assertThat(e.isSuccess).isEqualTo(errorScenario.isSuccess)
                assertThat(e.isFailure).isEqualTo(errorScenario.isFailure)
                assertThat(e.isUnknown).isEqualTo(errorScenario.isUnknown)
            }
        }
    }

    private fun generateErrorScenarios(): List<ErrorScenario> {
        return TossPaymentError.entries.map { error ->
            ErrorScenario(
                errorCode = error.name,
                isSuccess = error.isSuccess(),
                isFailure = error.isFailure(),
                isUnknown = error.isUnknown()
            )
        }
    }

}


data class ErrorScenario(
    val errorCode: String,
    val isSuccess: Boolean,
    val isFailure: Boolean,
    val isUnknown: Boolean,
)