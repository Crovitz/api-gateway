package io.emkro.apigateway

import org.bson.types.ObjectId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AuthorizationRepository : CoroutineCrudRepository<Authorization, ObjectId>
