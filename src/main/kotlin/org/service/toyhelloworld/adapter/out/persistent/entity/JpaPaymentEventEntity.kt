package org.service.toyhelloworld.adapter.out.persistent.entity

import jakarta.persistence.*
import org.service.toyhelloworld.domain.payment.PaymentMethod
import org.service.toyhelloworld.domain.payment.PaymentType
import java.time.LocalDateTime

@Table(name = "payment_events")
@Entity
class JpaPaymentEventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "buyer_id", nullable = false)
    val buyerId: Long,

    @Column(name = "is_payment_done", nullable = false)
    val isPaymentDone: Boolean = false,

    @Column(name = "payment_key", unique = true)
    val paymentKey: String? = null,

    @Column(name = "order_id", unique = true)
    val orderId: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: PaymentType = PaymentType.NORMAL,

    @Column(name = "order_name", nullable = false)
    val orderName: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "method")
    val method: PaymentMethod? = null,

    @Column(name = "psp_raw_data", columnDefinition = "JSON")
    val pspRawData: String? = null,

    @Column(name = "approved_at")
    val approvedAt: LocalDateTime? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()

)