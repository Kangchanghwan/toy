package org.service.toyhelloworld.adapter.`in`.web.view

import org.service.toyhelloworld.adapter.`in`.web.view.request.CheckOutRequest
import org.service.toyhelloworld.application.port.`in`.CheckOutCommand
import org.service.toyhelloworld.application.port.`in`.CheckOutUseCase
import org.service.toyhelloworld.common.IdempotencyCreator
import org.service.toyhelloworld.common.WebAdapter
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
@WebAdapter
class CheckOutController(
    private val checkOutUseCase: CheckOutUseCase
) {


    @GetMapping("/")
    fun checkout(request: CheckOutRequest, model: Model): String {
        val command = CheckOutCommand(
            cartId = request.cartId,
            buyerId = request.buyerId,
            productIds = request.productIds,
            idempotencyKey = IdempotencyCreator.create(request.seed)
        )
        val checkOutResult = checkOutUseCase.checkOut(command)
        model.addAttribute("orderId", checkOutResult.orderId)
        model.addAttribute("orderName", checkOutResult.orderName)
        model.addAttribute("amount", checkOutResult.amount)
        return "checkout"
    }
}