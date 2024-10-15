package org.service.toyhelloworld.adapter.test

import org.service.toyhelloworld.domain.payment.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.support.TransactionTemplate
import java.sql.ResultSet

class JdbcPaymentDatabaseHelper(
    private val jdbcTemplate: JdbcTemplate,
    private val transactionTemplate: TransactionTemplate,
) : PaymentDatabaseHelper {
    override fun getPayments(orderId: String): PaymentEvent? {
        return jdbcTemplate.queryForObject<PaymentEvent>(
            SELECT_PAYMENT_EVENT_QUERY,
            paymentEventRowMapper(orderId),
            orderId
        )
    }

    override fun clean() {
        transactionTemplate.execute {
            deletePaymentOrderHistories()
            deletePaymentOrders()
            deletePaymentEvents()
            deleteOutboxes()
        }
    }

    private fun paymentEventRowMapper(orderId: String) = { rs: ResultSet, _: Int ->
        PaymentEvent(
            id = rs.getLong("id"),
            orderName = rs.getString("order_name"),
            orderId = rs.getString("order_id"),
            buyerId = rs.getLong("buyer_id"),
            paymentKey = rs.getString("payment_key"),
            paymentType = rs.getString("type")?.let { PaymentType.get(it) },
            paymentMethod = rs.getString("method")?.let { PaymentMethod.get(it) },
            approvedAt = rs.getTimestamp("approved_at")?.toLocalDateTime(),
            isPaymentDone = rs.getByte("is_payment_done").toInt() == 1,
            paymentOrders = jdbcTemplate.query(SELECT_PAYMENT_ORDER_QUERY, paymentOrderRowMapper(), orderId)
        )
    }

    private fun paymentOrderRowMapper() = { rs: ResultSet, _: Int ->
        PaymentOrder(
            id = rs.getLong("id"),
            paymentEventId = rs.getLong("payment_event_id"),
            sellerId = rs.getLong("seller_id"),
            orderId = rs.getString("order_id"),
            productId = rs.getLong("product_id"),
            amount = rs.getBigDecimal("amount").toLong(),
            paymentStatus = rs.getString("payment_order_status").let { PaymentStatus.get(it) },
            isLegerUpdated = rs.getByte("ledger_updated").toInt() == 1,
            isWalletUpdated = rs.getByte("wallet_updated").toInt() == 1
        )
    }

    private fun deletePaymentOrderHistories() = jdbcTemplate.update(DELETE_PAYMENT_ORDER_HISTORY_QUERY)

    private fun deletePaymentOrders() = jdbcTemplate.update(DELETE_PAYMENT_ORDER_QUERY)

    private fun deletePaymentEvents() = jdbcTemplate.update(DELETE_PAYMENT_EVENT_QUERY)

    private fun deleteOutboxes() = jdbcTemplate.update(DELETE_OUTBOX_QUERY)

    companion object {
        val SELECT_PAYMENT_EVENT_QUERY = """
            SELECT * FROM payment_events pe
            WHERE pe.order_id=?
        """.trimIndent()

        val SELECT_PAYMENT_ORDER_QUERY = """
            SELECT * FROM payment_orders po
            WHERE po.order_id=?
        """.trimIndent()

        val DELETE_PAYMENT_EVENT_QUERY = """
            DELETE FROM payment_events
        """.trimIndent()
        val DELETE_PAYMENT_ORDER_QUERY = """
            DELETE FROM payment_orders            
        """.trimIndent()
        val DELETE_PAYMENT_ORDER_HISTORY_QUERY = """
            DELETE FROM payment_order_histories
        """.trimIndent()
        val DELETE_OUTBOX_QUERY = """
            DELETE FROM outboxes
        """.trimIndent()
    }

}