package io.emkro.apigateway

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@Document("authorizations")
@TypeAlias("Authorization")
data class Authorization internal constructor(
    @Id val id: ObjectId,
    val matcher: String,
    var authority: String,
) {
    constructor(matcher: String, authority: String) : this(ObjectId.get(), matcher, authority)
}
