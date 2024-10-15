package org.service.toyhelloworld.adapter.out.web

import org.service.toyhelloworld.adapter.out.web.client.ProductClient
import org.service.toyhelloworld.application.port.out.LoadProductPort
import org.service.toyhelloworld.common.WebAdapter
import org.service.toyhelloworld.domain.payment.Product

@WebAdapter
class ProductWebAdapter(
    private val productClient: ProductClient
): LoadProductPort{
    override fun getProducts(cartId: Long, productIds: List<Long>): List<Product> {
        return productClient.getProducts(cartId, productIds)
    }
}