package org.service.toyhelloworld

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class ToyHelloworldApplication

fun main(args: Array<String>) {
    runApplication<ToyHelloworldApplication>(*args)
}



