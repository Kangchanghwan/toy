package org.service.toyhelloworld.application.service

import org.service.toyhelloworld.application.port.`in`.PaymentConfirmCommand
import org.service.toyhelloworld.application.port.`in`.PaymentConfirmUseCase
import org.service.toyhelloworld.application.port.out.PaymentExecutionPort
import org.service.toyhelloworld.application.port.out.PaymentStatusUpdateCommand
import org.service.toyhelloworld.application.port.out.PaymentStatusUpdatePort
import org.service.toyhelloworld.application.port.out.PaymentValidationPort
import org.service.toyhelloworld.common.UseCase
import org.service.toyhelloworld.domain.payment.PaymentConfirmationResult

@UseCase
class PaymentConfirmService(
    private val paymentStatusUpdatePort: PaymentStatusUpdatePort,
    private val paymentValidationPort: PaymentValidationPort,
    private val paymentExecutionPort: PaymentExecutionPort,
    private val paymentErrorHandler: PaymentErrorHandler,
) : PaymentConfirmUseCase {

    override fun confirm(command: PaymentConfirmCommand): PaymentConfirmationResult {
       return try {
            paymentStatusUpdatePort.updatePaymentStatusToExecuting(command.orderId, command.paymentKey)
            paymentValidationPort.isValid(command.orderId, command.amount)
            val paymentExecutionResult = paymentExecutionPort.execute(command)
            paymentStatusUpdatePort.updatePaymentStatus(
                PaymentStatusUpdateCommand(
                    paymentKey = paymentExecutionResult.paymentKey,
                    orderId = paymentExecutionResult.orderId,
                    status = paymentExecutionResult.paymentStatus(),
                    extraDetails = paymentExecutionResult.extraDetails,
                    failure = paymentExecutionResult.failure
                )
            )
            return PaymentConfirmationResult(
                status = paymentExecutionResult.paymentStatus(),
                failure = paymentExecutionResult.failure
            )
        } catch (e: Throwable) {
            paymentErrorHandler.handlePaymentConfirmationError(e, command)
        }
    }



}