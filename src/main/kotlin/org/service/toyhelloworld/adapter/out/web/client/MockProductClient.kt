package org.service.toyhelloworld.adapter.out.web.client

import org.service.toyhelloworld.domain.payment.Product
import org.springframework.stereotype.Component

@Component
class MockProductClient : ProductClient {
    override fun getProducts(cartId: Long, productIds: List<Long>): List<Product> {
        return productIds.map {
            Product(
                id = it,
                amount = it * 10_000,
                quantity = 2,
                name = "test_product_$it",
                sellerId = 1L
            )
        }
    }
}