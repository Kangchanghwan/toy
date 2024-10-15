package org.service.toyhelloworld.domain.payment

enum class PaymentType(val description: String) {
    NORMAL("일반 결제");

    companion object {
        fun get(type: String): PaymentType {
            return entries.find { it.name == type }
                ?: error("PaymentType (Type: $type) 은 올바르지 않은 결제 타입입니다..)")
        }
    }
}
