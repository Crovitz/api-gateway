package io.emkro.apigateway

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/authorizations")
class AuthorizationController(
    private val authorizationRepository: AuthorizationRepository,
) {

    @GetMapping
    fun findAll() = authorizationRepository.findAll()
}
