package org.service.toyhelloworld.adapter.out.persistent.entity

import jakarta.persistence.*
import org.service.toyhelloworld.domain.payment.PaymentStatus
import java.time.LocalDateTime

@Entity
@Table(name = "payment_order_histories")
class JpaPaymentOrderHistoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "payment_order_id", nullable = false)
    val paymentOrderId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    val previousStatus: PaymentStatus? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    val newStatus: PaymentStatus,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "changed_by")
    val changedBy: String? = null,

    @Column(name = "reason")
    val reason: String? = null
) {


}