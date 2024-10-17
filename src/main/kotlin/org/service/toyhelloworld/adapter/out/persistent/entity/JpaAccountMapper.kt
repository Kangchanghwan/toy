package org.service.toyhelloworld.adapter.out.persistent.entity

import org.service.toyhelloworld.domain.ledger.Account
import org.springframework.stereotype.Component

@Component
class JpaAccountMapper {

    fun mapToDomainEntity(entity: JpaAccountEntity): Account {
        return Account(
            id = entity.id!!,
            name = entity.name
        )
    }
}