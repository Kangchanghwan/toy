package org.service.toyhelloworld.adapter.`in`.web.toss.executor

import org.service.toyhelloworld.adapter.`in`.web.toss.exception.PSPConfirmationException
import org.service.toyhelloworld.adapter.`in`.web.toss.exception.TossPaymentError
import org.service.toyhelloworld.adapter.`in`.web.toss.repsonse.TossFailureResponse
import org.service.toyhelloworld.adapter.`in`.web.toss.repsonse.TossPaymentConfirmResponse
import org.service.toyhelloworld.application.port.`in`.PaymentConfirmCommand
import org.service.toyhelloworld.common.objectMapper
import org.service.toyhelloworld.domain.payment.*
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class TossPaymentExecutor(
    private val tossPaymentRestClient: RestClient,
    private val uri: String = "/v1/payments/confirm",
) : PaymentExecutor {

    override fun execute(command: PaymentConfirmCommand): PaymentExecutionResult {
        return tossPaymentRestClient.post()
            .uri(uri)
            .header("Idempotency-Key", command.orderId)
            .body(
                """
                    {
                        "paymentKey": "${command.paymentKey}",
                        "orderId": "${command.orderId}",
                        "amount": ${command.amount}
                    }
                """.trimIndent()
            )
            .retrieve()
            .onStatus({ statusCode: HttpStatusCode -> statusCode.is4xxClientError || statusCode.is5xxServerError }) { _, response ->
                val tossFailureResponse = objectMapper.readValue(response.body, TossFailureResponse::class.java)
                val error = TossPaymentError.get(tossFailureResponse.code)
                throw PSPConfirmationException(
                    errorCode = error.name,
                    errorMessage = error.description,
                    isSuccess = error.isSuccess(),
                    isFailure = error.isFailure(),
                    isUnknown = error.isUnknown(),
                    isRetryableError = error.isRetryableError()
                )
            }
            .body(TossPaymentConfirmResponse::class.java)
            .let {
                PaymentExecutionResult(
                    paymentKey = it!!.paymentKey,
                    orderId = it.orderId,
                    extraDetails = PaymentExtraDetails(
                        type = PaymentType.get(it.type),
                        method = PaymentMethod.get(it.method),
                        approvedAt = LocalDateTime.parse(it.approvedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                        pspRawData = it.toString(),
                        orderName = it.orderName,
                        pspConfirmationStatus = PSPConfirmationsStatus.get(it.status),
                        totalAmount = it.totalAmount.toLong(),
                    ),
                    isSuccess = true,
                    isFailure = false,
                    isUnknown = false,
                    isRetryable = false
                )
            }
    }
}