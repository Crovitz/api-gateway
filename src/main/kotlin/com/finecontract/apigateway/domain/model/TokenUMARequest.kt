package com.finecontract.apigateway.domain.model

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

class TokenUMARequest(tokenOIDC: String, oAuth2Client: OAuth2ClientProperties) {
    val parameters = getParameters(oAuth2Client)
    val headers = getHeaders(tokenOIDC)
}

private fun getParameters(oAuth2Client: OAuth2ClientProperties): MultiValueMap<String, String> {
    val clientId = oAuth2Client.registration["main"]?.clientId.orEmpty()
    val parameters: MultiValueMap<String, String> = LinkedMultiValueMap()
    parameters.add("audience", clientId)
    parameters.add("grant_type", "urn:ietf:params:oauth:grant-type:uma-ticket")
    return parameters
}

private fun getHeaders(tokenOIDC: String): MultiValueMap<String, String> {
    val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
    headers.add(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
    headers.add(AUTHORIZATION, "Bearer $tokenOIDC")
    return headers
}
