package org.service.toyhelloworld.adapter.`in`.web.toss

import org.service.toyhelloworld.adapter.`in`.web.toss.executor.PaymentExecutor
import org.service.toyhelloworld.application.port.`in`.PaymentConfirmCommand
import org.service.toyhelloworld.application.port.out.PaymentExecutionPort
import org.service.toyhelloworld.common.WebAdapter
import org.service.toyhelloworld.domain.payment.PaymentExecutionResult

@WebAdapter
class PaymentExecutorWebAdapter(
    private val paymentExecutor: PaymentExecutor
) : PaymentExecutionPort {
    override fun execute(command: PaymentConfirmCommand): PaymentExecutionResult {
        return paymentExecutor.execute(command)
    }

}