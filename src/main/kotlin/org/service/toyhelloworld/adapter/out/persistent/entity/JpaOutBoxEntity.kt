package org.service.toyhelloworld.adapter.out.persistent.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "outboxes")
class JpaOutBoxEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "idempotency_key", nullable = false)
    val idempotencyKey: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: OutboxStatus = OutboxStatus.INIT,

    @Column(name = "type")
    val type: String? = null,

    @Column(name = "partition_key", nullable = false)
    val partitionKey: Int = 0,

    @Column(name = "payload", columnDefinition = "JSON")
    val payload: String? = null,

    @Column(name = "metadata", columnDefinition = "JSON")
    val metadata: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class OutboxStatus {
    INIT,
    FAILURE,
    SUCCESS
}