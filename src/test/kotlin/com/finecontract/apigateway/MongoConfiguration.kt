package com.finecontract.apigateway

import com.mongodb.ConnectionString
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.ImmutableMongodConfig
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version.Main.V5_0
import de.flapdoodle.embed.process.runtime.Network.freeServerPort
import de.flapdoodle.embed.process.runtime.Network.getLocalHost
import de.flapdoodle.embed.process.runtime.Network.localhostIsIPv6
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
internal class MongoConfiguration {

    @Bean
    fun mongodConfig(): ImmutableMongodConfig {
        return ImmutableMongodConfig.builder()
            .version(V5_0)
            .net(Net(freeServerPort(getLocalHost()), localhostIsIPv6()))
            .build()
    }

    @Bean(destroyMethod = "stop")
    fun mongodExecutable(mongodConfig: ImmutableMongodConfig): MongodExecutable {
        val starter = MongodStarter.getDefaultInstance()
        return starter.prepare(mongodConfig)
    }

    @Bean(destroyMethod = "stop")
    fun mongodProcess(mongodExecutable: MongodExecutable): MongodProcess {
        return mongodExecutable.start()
    }

    @Bean
    fun mongoClient(): MongoClient? {
        val mongodConfig = mongodConfig()
        mongodProcess(mongodExecutable(mongodConfig))
        return MongoClients.create(ConnectionString("mongodb://localhost:" + mongodConfig.net().port))
    }
}
