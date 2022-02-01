package com.finecontract.apigateway.infrastructure

import com.finecontract.apigateway.domain.Access
import org.bson.types.ObjectId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AccessRepository : CoroutineCrudRepository<Access, ObjectId>
