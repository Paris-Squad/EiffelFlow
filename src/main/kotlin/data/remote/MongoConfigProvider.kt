package org.example.data.remote

import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.bson.UuidRepresentation
import org.bson.codecs.UuidCodec
import org.bson.codecs.configuration.CodecRegistries.fromCodecs
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.example.domain.utils.KotlinxLocalDateTimeCodec

class MongoConfigProvider {
    fun getDatabase(): MongoDatabase {
        val defaultRegistry = MongoClientSettings.getDefaultCodecRegistry()
        val customRegistry = fromCodecs(KotlinxLocalDateTimeCodec())
        val uuidRegistry = fromCodecs(
            UuidCodec(UuidRepresentation.STANDARD)
        )
        val codecRegistry = fromRegistries(uuidRegistry, customRegistry, defaultRegistry)

        val connectionString = getConnectionString()
        val databaseName = System.getenv("DATABASE_NAME")
        return MongoClient.create(connectionString)
            .getDatabase(databaseName)
            .withCodecRegistry(codecRegistry)
    }

    private fun getConnectionString(): String {
        val protocol = System.getenv("MONGO_PROTOCOL")
        val userName = System.getenv("MONGO_USER_NAME")
        val password = System.getenv("MONGO_PASSWORD")
        val hostName = System.getenv("MONGO_HOSTNAME")
        val connectionOptions = System.getenv("MONGO_CONNECTION_OPTIONS")
        val appName = System.getenv("MONGO_APP_NAME")

        val connectionString = "$protocol://$userName:$password@$hostName/$connectionOptions&appName=$appName"
        return connectionString
    }
}