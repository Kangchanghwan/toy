package org.service.toyhelloworld.adapter.out.persistent.repository

import org.service.toyhelloworld.adapter.out.persistent.entity.JpaOutBoxEntity
import org.springframework.data.jpa.repository.JpaRepository

class JpaOutBoxRepository {
}
interface SpringDataJpaOutBoxRepository : JpaRepository<JpaOutBoxEntity,Long>