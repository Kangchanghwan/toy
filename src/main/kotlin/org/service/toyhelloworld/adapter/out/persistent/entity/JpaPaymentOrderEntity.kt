package org.service.toyhelloworld.adapter.out.persistent.entity

import jakarta.persistence.*
import org.service.toyhelloworld.domain.payment.PaymentStatus
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "payment_orders")
class JpaPaymentOrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "payment_event_id", nullable = false)
    val paymentEvent: JpaPaymentEventEntity,

    @Column(name = "seller_id", nullable = false)
    val sellerId: Long,

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(name = "order_id", nullable = false)
    val orderId: String,

    @Column(name = "amount", nullable = false)
    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_order_status", nullable = false)
    val paymentOrderStatus: PaymentStatus = PaymentStatus.NOT_STARTED,

    @Column(name = "ledger_updated", nullable = false)
    val ledgerUpdated: Boolean = false,

    @Column(name = "wallet_updated", nullable = false)
    val walletUpdated: Boolean = false,

    @Column(name = "failed_count", nullable = false)
    val failedCount: Int = 0,

    @Column(name = "threshold", nullable = false)
    val threshold: Int = 5,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)