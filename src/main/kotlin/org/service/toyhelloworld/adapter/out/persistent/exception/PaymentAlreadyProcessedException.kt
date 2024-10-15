package org.service.toyhelloworld.adapter.out.persistent.exception

import org.service.toyhelloworld.domain.payment.PaymentStatus

class PaymentAlreadyProcessedException(
    val status: PaymentStatus,
    message: String
): RuntimeException(message)