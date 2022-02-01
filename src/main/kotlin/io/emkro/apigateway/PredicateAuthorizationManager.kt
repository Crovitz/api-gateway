package io.emkro.apigateway

import io.emkro.apigateway.Predicate.PERMISSION
import io.emkro.apigateway.Predicate.REALM_ROLE
import io.emkro.apigateway.Predicate.USER_PATH
import org.springframework.http.server.PathContainer
import org.springframework.security.authorization.AuthorityAuthorizationDecision
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.ReactiveAuthorizationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.web.server.authorization.AuthorizationContext
import reactor.core.publisher.Mono

class PredicateAuthorizationManager<T> internal constructor(
    private var authorities: List<GrantedAuthority>,
) : ReactiveAuthorizationManager<T> {
    constructor(vararg authorities: String) : this(AuthorityUtils.createAuthorityList(*authorities))

    override fun check(authentication: Mono<Authentication>, `object`: T): Mono<AuthorizationDecision> {
        return authentication.filter { auth: Authentication -> auth.isAuthenticated }
            .flatMapIterable { auth: Authentication -> auth.authorities }
            .map { grantedAuthority: GrantedAuthority -> grantedAuthority.authority }
            .any { grantedAuthority: String ->
                authorities.stream().anyMatch { authority: GrantedAuthority -> authority.authority == grantedAuthority }
            }
            .map { granted: Boolean -> AuthorityAuthorizationDecision(granted, this.authorities) as AuthorizationDecision }
            .defaultIfEmpty(AuthorityAuthorizationDecision(false, this.authorities))
    }

    companion object Access {
        fun authorize(authorization: Authorization): ReactiveAuthorizationManager<AuthorizationContext> {
            return when (authorization.predicate) {
                PERMISSION -> hasPermission(authorization.authority)
                REALM_ROLE -> hasRealmRole(authorization.authority)
                USER_PATH -> isUser(authorization.pathSegment)
            }
        }

        private fun hasPermission(authority: String): PredicateAuthorizationManager<AuthorizationContext> {
            return PredicateAuthorizationManager(authority)
        }

        private fun hasRealmRole(authority: String): PredicateAuthorizationManager<AuthorizationContext> {
            return PredicateAuthorizationManager("ROLE_$authority")
        }

        private fun isUser(pathSegment: Int?): ReactiveAuthorizationManager<AuthorizationContext> {
            return pathSegment?.let {
                ReactiveAuthorizationManager { authentication: Mono<Authentication>, context: AuthorizationContext ->
                    val request = context.exchange.request.path
                    val segments = request.elements().filterIsInstance<PathContainer.PathSegment>()
                    if (pathSegment in 1..segments.size) {
                        val user = segments[pathSegment - 1].value()
                        return@ReactiveAuthorizationManager authentication.map {
                            val token = it.principal as Jwt
                            AuthorizationDecision(token.subject.equals(user))
                        }
                    }
                    Mono.just(AuthorizationDecision(false))
                }
            }
                ?: ReactiveAuthorizationManager { _: Mono<Authentication>, _: AuthorizationContext ->
                    Mono.just(AuthorizationDecision(false))
                }
        }
    }
}
