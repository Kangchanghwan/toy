package org.service.toyhelloworld.adapter.`in`.web.toss.exception

import org.service.toyhelloworld.domain.payment.PaymentStatus

class PSPConfirmationException(
    val errorCode: String,
    val errorMessage: String,
    val isSuccess: Boolean,
    val isFailure: Boolean,
    val isUnknown: Boolean,
    val isRetryableError: Boolean,
    cause: Throwable? = null
) : RuntimeException(errorMessage, cause) {

    init {
        require(listOf(isSuccess || isFailure || isUnknown).count { it } == 1) {
            "${this::class.simpleName} 는 올바르지 않은 결재 상태를 가지고 있습니다."
        }
    }

    fun paymentStatus(): PaymentStatus {
        return when {
            isSuccess -> PaymentStatus.SUCCESS
            isFailure -> PaymentStatus.FAILURE
            isUnknown -> PaymentStatus.UNKNOWN
            else -> error("${this::class.simpleName} 는 올바르지 않은 결재 상태를 가지고 있습니다.")
        }
    }

}