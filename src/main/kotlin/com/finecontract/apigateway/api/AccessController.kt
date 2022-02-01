package com.finecontract.apigateway.api

import com.finecontract.apigateway.infrastructure.AccessRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/accesses")
class AccessController(private val accessRepository: AccessRepository) {

    @GetMapping
    fun findAll() = accessRepository.findAll()
}
