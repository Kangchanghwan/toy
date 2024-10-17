package org.service.toyhelloworld.application.port.out

import org.service.toyhelloworld.domain.payment.PaymentEventMessage


interface DuplicateLegerMessageFilterPort {
    fun isAlreadyProcess(message: PaymentEventMessage) : Boolean
}