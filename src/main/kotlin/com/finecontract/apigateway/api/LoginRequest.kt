package com.finecontract.apigateway.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.finecontract.apigateway.domain.model.GrantType
import com.finecontract.apigateway.domain.model.GrantType.PASSWORD
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@JsonIgnoreProperties(ignoreUnknown = true)
data class LoginRequest(

    @field:NotBlank
    @field:Size(min = 3, max = 30)
    val username: String,

    @field:NotBlank
    @field:Size(min = 8, max = 30)
    val password: String,

    @field:NotBlank
    val clientSecret: String,

    val grantType: GrantType = PASSWORD,

    val refreshToken: String?,
)
