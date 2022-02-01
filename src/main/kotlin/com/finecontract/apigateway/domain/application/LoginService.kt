package com.finecontract.apigateway.domain.application

import com.finecontract.apigateway.api.LoginRequest
import com.finecontract.apigateway.domain.model.GrantType.PASSWORD
import com.finecontract.apigateway.domain.model.GrantType.REFRESH_TOKEN
import com.finecontract.apigateway.domain.model.TokenUMARequest
import com.nimbusds.jose.shaded.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

@Service
class LoginService(
    private val keycloakService: KeycloakService,
    private val oAuth2Client: OAuth2ClientProperties,
) {

    @Value("\${keycloak.client-id}")
    lateinit var keycloakClientId: String

    suspend fun login(request: LoginRequest): JSONObject {
        val parameters: MultiValueMap<String, String> = extractOIDCParameters(request)
        val tokenOIDC = keycloakService.getTokenOIDC(parameters)["access_token"] as String
        return keycloakService.getTokenUMA(TokenUMARequest(tokenOIDC, oAuth2Client))
    }

    private fun extractOIDCParameters(request: LoginRequest): MultiValueMap<String, String> {
        val parameters: MultiValueMap<String, String> = LinkedMultiValueMap()
        val grantType = request.grantType

        parameters.add("grant_type", grantType.name.lowercase())
        parameters.add("client_id", keycloakClientId)
        parameters.add("client_secret", request.clientSecret)

        when (grantType) {
            PASSWORD -> {
                parameters.add("username", request.username)
                parameters.add("password", request.password)
            }
            REFRESH_TOKEN -> parameters.add("refresh_token", request.refreshToken)
        }
        return parameters
    }
}
