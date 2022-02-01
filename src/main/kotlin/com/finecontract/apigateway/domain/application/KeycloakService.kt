package com.finecontract.apigateway.domain.application

import com.finecontract.apigateway.domain.model.TokenUMARequest
import com.nimbusds.jose.shaded.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange

@Service
class KeycloakService(
    private val oAuth2Client: OAuth2ClientProperties,
) {

    @Value("\${spring.security.oauth2.client.provider.keycloak.token-uri}")
    lateinit var tokenEndpoint: String

    suspend fun getTokenOIDC(request: MultiValueMap<String, String>): JSONObject {
        return WebClient.create()
            .post()
            .uri(tokenEndpoint)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(request)
            .awaitExchange {
                if (it.statusCode() == OK) {
                    return@awaitExchange it.awaitBody<JSONObject>()
                }
                throw IllegalArgumentException("Unauthenticated")
            }
    }

    suspend fun getTokenUMA(request: TokenUMARequest): JSONObject {
        val tokenUri = oAuth2Client.provider["keycloak"]?.tokenUri.orEmpty()
        return WebClient.create()
            .post()
            .uri(tokenUri)
            .headers { it.addAll(request.headers) }
            .bodyValue(request.parameters)
            .awaitExchange {
                if (it.statusCode() == OK) {
                    return@awaitExchange it.awaitBody<JSONObject>()
                }
                throw IllegalArgumentException("Unauthorized")
            }
    }
}
