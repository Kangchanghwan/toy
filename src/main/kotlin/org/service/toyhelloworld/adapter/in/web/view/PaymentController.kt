package org.service.toyhelloworld.adapter.`in`.web.view

import org.service.toyhelloworld.common.WebAdapter
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
@WebAdapter
class PaymentController {

    @GetMapping("/success")
    fun successPage(): String {
        return "success"
    }


    @GetMapping("/fail")
    fun failPage(): String {
        return "fail"
    }

}