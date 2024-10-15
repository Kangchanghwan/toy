package org.service.toyhelloworld.domain.payment

data class PaymentOrder(
    val id: Long? = null,
    val paymentEventId: Long? = null,
    val sellerId: Long,
    val productId: Long,
    val orderId: String,
    val amount: Long,
    val paymentStatus: PaymentStatus,
    private var isLegerUpdated: Boolean = false,
    private var isWalletUpdated: Boolean = false
){
    fun isLegerUpdated(): Boolean = isLegerUpdated
    fun isWalletUpdated(): Boolean = isWalletUpdated
    fun confirmWalletUpdate() {
        isWalletUpdated = true
    }

    fun confirmLedgerUpdate() {
        isLegerUpdated = true
    }
}
