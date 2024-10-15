package org.service.toyhelloworld.adapter.out.web.client

import org.service.toyhelloworld.domain.payment.Product

interface ProductClient {
    fun getProducts(cartId: Long, productIds: List<Long>): List<Product>
}