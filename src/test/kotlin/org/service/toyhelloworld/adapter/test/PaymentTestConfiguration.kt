package org.service.toyhelloworld.adapter.test

import jakarta.activation.DataSource
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.support.TransactionTemplate

@TestConfiguration
class PaymentTestConfiguration {

    @Bean
    fun paymentDatabaseHelper(jdbcTemplate: JdbcTemplate, transactionTemplate: TransactionTemplate): PaymentDatabaseHelper{
        return JdbcPaymentDatabaseHelper(jdbcTemplate, transactionTemplate)
    }

}