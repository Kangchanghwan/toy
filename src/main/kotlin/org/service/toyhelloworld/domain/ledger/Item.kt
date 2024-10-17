package org.service.toyhelloworld.domain.ledger


open class Item(
    val id: Long,
    val amount: Long,
    val orderId: String,
    val type: ReferenceType,
    )
