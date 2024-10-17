package org.service.toyhelloworld.adapter.out.persistent.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "accounts")
class JpaAccountEntity(
    @Id
    val id: Long? = null,

    val name: String,
)