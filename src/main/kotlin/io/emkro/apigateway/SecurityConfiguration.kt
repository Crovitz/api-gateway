package io.emkro.apigateway

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Bean
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import reactor.core.publisher.Mono

@EnableWebFluxSecurity
class SecurityConfiguration {

    @RefreshScope
    @Bean
    fun springSecurityFilterChain(
        http: ServerHttpSecurity,
        permissionTicketConverter: PermissionTicketConverter,
        authorizationRepository: AuthorizationRepository,
    ): SecurityWebFilterChain {
        val authorizations = authorizationRepository.findAll()
        return http {
            authorizeExchange {
                runBlocking {
                    launch {
                        authorizations.collect {
                            authorize(PathPatternParserServerWebExchangeMatcher(it.matcher, it.method), PredicateAuthorizationManager.authorize(it))
                        }
                        authorize(anyExchange, denyAll)
                    }
                }
            }
            oauth2ResourceServer {
                jwt { jwtAuthenticationConverter = extractGrantedAuthorities(permissionTicketConverter) }
            }
        }
    }

    fun extractGrantedAuthorities(permissionTicketConverter: PermissionTicketConverter): Converter<Jwt, Mono<AbstractAuthenticationToken>> {
        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(permissionTicketConverter)
        return ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter)
    }
}
