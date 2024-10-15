package org.service.toyhelloworld.application.service

import org.service.toyhelloworld.application.port.`in`.CheckOutCommand
import org.service.toyhelloworld.application.port.`in`.CheckOutUseCase
import org.service.toyhelloworld.application.port.out.LoadProductPort
import org.service.toyhelloworld.application.port.out.SavePaymentPort
import org.service.toyhelloworld.common.UseCase
import org.service.toyhelloworld.domain.payment.*

@UseCase
class CheckOutService(
    private val loadProductPort: LoadProductPort,
    private val savePaymentPort: SavePaymentPort,
) : CheckOutUseCase {

    override fun checkOut(command: CheckOutCommand): CheckOutResult {
        val products = loadProductPort.getProducts(command.cartId, command.productIds)
        val paymentEvent = createPaymentEvent(command, products)
        savePaymentPort.save(paymentEvent)
        return CheckOutResult(
            amount = paymentEvent.totalAmount(),
            orderId = paymentEvent.orderId,
            orderName = paymentEvent.orderName
        )
    }

    private fun createPaymentEvent(command: CheckOutCommand, products: List<Product>): PaymentEvent {
        return PaymentEvent(
            buyerId = command.buyerId,
            orderId = command.idempotencyKey,
            orderName = products.joinToString { it.name },
            paymentOrders = products.map {
                PaymentOrder(
                    sellerId = it.sellerId,
                    orderId = command.idempotencyKey,
                    productId = it.id,
                    amount = it.amount,
                    paymentStatus = PaymentStatus.NOT_STARTED,
                )
            }
        )
    }
}