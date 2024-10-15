
package org.service.toyhelloworld.application.port.out

import org.service.toyhelloworld.domain.payment.Product

interface LoadProductPort {

    fun getProducts(cartId: Long, productIds: List<Long>): List<Product>

}