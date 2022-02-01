package com.finecontract.apigateway.domain.application

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class PermissionTicketConverter : Converter<Jwt, Collection<GrantedAuthority>> {
    override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
        val permissions = toPermissions(jwt)
        val roles = toRoles(jwt)

        return permissions + roles
    }

    private fun toPermissions(jwt: Jwt): List<SimpleGrantedAuthority> {
        val authorization = jwt.claims["authorization"] as Map<*, *>?
        val permissionList = authorization?.get("permissions") as List<Map<*, *>>?
        val scopeArrays = permissionList
            ?.mapNotNull { permission -> permission["scopes"] }
            ?.toList() as List<List<String>>?
        val scopes = scopeArrays?.flatten()

        return scopes
            ?.map { SimpleGrantedAuthority(it) }
            ?.toList() ?: emptyList()
    }

    private fun toRoles(jwt: Jwt): List<SimpleGrantedAuthority> {
        val realm = jwt.claims["realm_access"] as Map<*, *>?
        val roles = realm?.get("roles") as List<*>?

        return roles
            ?.map { role -> "ROLE_$role" }
            ?.map { SimpleGrantedAuthority(it) }
            ?.toList() ?: emptyList()
    }
}
