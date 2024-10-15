package org.service.toyhelloworld.adapter.out.persistent

import org.service.toyhelloworld.adapter.out.persistent.repository.PaymentEventRepository
import org.service.toyhelloworld.adapter.out.persistent.repository.PaymentOrderRepository
import org.service.toyhelloworld.application.port.out.*
import org.service.toyhelloworld.common.PersistentAdapter
import org.service.toyhelloworld.domain.payment.PaymentEvent

@PersistentAdapter
class PaymentCompletePersistentAdapter(
    private val paymentEventRepository: PaymentEventRepository,
    private val paymentOrderRepository: PaymentOrderRepository,
) : SavePaymentPort, PaymentStatusUpdatePort, PaymentValidationPort {
    override fun save(paymentEvent: PaymentEvent) {
        paymentEventRepository.save(paymentEvent)
    }

    override fun updatePaymentStatusToExecuting(orderId: String, paymentKey: String) {
        paymentOrderRepository.updatePaymentStatusToExecuting(orderId, paymentKey)
    }

    override fun updatePaymentStatus(command: PaymentStatusUpdateCommand) {
        paymentOrderRepository.updatePaymentStatus(command)
    }

    override fun isValid(orderId: String, amount: Long) {
        paymentOrderRepository.isValid(orderId, amount)
    }
}