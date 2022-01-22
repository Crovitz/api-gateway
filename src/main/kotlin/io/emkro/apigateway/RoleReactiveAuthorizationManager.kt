package io.emkro.apigateway

import org.springframework.security.authorization.AuthorityAuthorizationDecision
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.ReactiveAuthorizationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import reactor.core.publisher.Mono

class RoleReactiveAuthorizationManager<T> internal constructor(
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

    companion object Predicate {
        fun <T> hasPermission(authority: String): RoleReactiveAuthorizationManager<T> {
            return RoleReactiveAuthorizationManager(authority)
        }

        fun <T> hasRealmRole(authority: String): RoleReactiveAuthorizationManager<T> {
            return RoleReactiveAuthorizationManager("ROLE_$authority")
        }
    }
}
