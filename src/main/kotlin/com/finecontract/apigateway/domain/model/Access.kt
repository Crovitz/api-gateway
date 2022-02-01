package com.finecontract.apigateway.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.http.HttpMethod

@Document("accesses")
@TypeAlias("Access")
data class Access internal constructor(
    @Id val id: ObjectId,
    val method: HttpMethod?,
    val matcher: String,
    val authority: String,
    val predicate: Predicate,
    val pathSegment: Int?,
) {
    constructor(method: HttpMethod, matcher: String, authority: String, predicate: Predicate, pathSegment: Int?)
            : this(ObjectId.get(), method, matcher, authority, predicate, pathSegment)
}

enum class Predicate {
    REALM_ROLE, PERMISSION, USER_PATH
}
