package com.finecontract.apigateway.api

import com.finecontract.apigateway.domain.application.LoginService
import com.nimbusds.jose.shaded.json.JSONObject
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/login")
class LoginController(private val loginService: LoginService) {

    @PostMapping
    suspend fun login(@Valid @RequestBody request: LoginRequest): JSONObject = loginService.login(request)
}
