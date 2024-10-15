package org.service.toyhelloworld.application.port.`in`

import org.service.toyhelloworld.domain.payment.CheckOutResult

interface CheckOutUseCase {
    fun checkOut(command: CheckOutCommand): CheckOutResult
}