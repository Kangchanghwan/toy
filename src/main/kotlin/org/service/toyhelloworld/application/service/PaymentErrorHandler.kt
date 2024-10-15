package org.service.toyhelloworld.application.service

import org.service.toyhelloworld.adapter.`in`.web.toss.exception.PSPConfirmationException
import org.service.toyhelloworld.adapter.out.persistent.exception.PaymentAlreadyProcessedException
import org.service.toyhelloworld.adapter.out.persistent.exception.PaymentValidationException
import org.service.toyhelloworld.application.port.`in`.PaymentConfirmCommand
import org.service.toyhelloworld.application.port.out.PaymentStatusUpdateCommand
import org.service.toyhelloworld.application.port.out.PaymentStatusUpdatePort
import org.service.toyhelloworld.domain.payment.PaymentConfirmationResult
import org.service.toyhelloworld.domain.payment.PaymentFailure
import org.service.toyhelloworld.domain.payment.PaymentStatus
import org.springframework.stereotype.Service
import java.util.concurrent.TimeoutException

@Service
class PaymentErrorHandler(
    private val paymentStatusUpdatePort: PaymentStatusUpdatePort,
) {

    fun handlePaymentConfirmationError(error: Throwable, command: PaymentConfirmCommand): PaymentConfirmationResult {
        val (status, failure) = when (error) {
            is PSPConfirmationException -> Pair(
                error.paymentStatus(),
                PaymentFailure(error.errorCode, error.errorMessage)
            )

            is PaymentValidationException -> Pair(
                PaymentStatus.FAILURE,
                PaymentFailure(error::class.simpleName ?: "", error.message ?: "")
            )

            is PaymentAlreadyProcessedException ->
                return PaymentConfirmationResult(
                    status = error.status,
                    PaymentFailure(error::class.simpleName ?: "", error.message ?: "")
                )

            is TimeoutException,
            -> Pair(
                PaymentStatus.UNKNOWN,
                PaymentFailure(error::class.simpleName ?: "", error.message ?: "")
            )

            else -> Pair(
                PaymentStatus.UNKNOWN,
                PaymentFailure(error::class.simpleName ?: "", error.message ?: "")
            )
        }

        val paymentStatusUpdateCommand = PaymentStatusUpdateCommand(
            paymentKey = command.paymentKey,
            orderId = command.orderId,
            status = status,
            failure = failure
        )
         paymentStatusUpdatePort.updatePaymentStatus(paymentStatusUpdateCommand)

         return PaymentConfirmationResult(status, failure)
    }

}