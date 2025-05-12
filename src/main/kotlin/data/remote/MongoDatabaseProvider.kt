package org.example.data.remote

import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.bson.UuidRepresentation
import org.bson.codecs.UuidCodec
import org.bson.codecs.configuration.CodecRegistries.fromCodecs
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.example.domain.exception.EiffelFlowException
import org.example.domain.utils.KotlinxLocalDateTimeCodec

class MongoDatabaseProvider(
    private val mongoClient: MongoClient
) {

    val database: MongoDatabase by lazy {
        mongoClient.getDatabase(getEnv(DATABASE_NAME))
            .withCodecRegistry(codecRegistry)
    }

    private val codecRegistry by lazy {
        val defaultRegistry = MongoClientSettings.getDefaultCodecRegistry()
        val customRegistry = fromCodecs(KotlinxLocalDateTimeCodec())
        val uuidRegistry = fromCodecs(UuidCodec(UuidRepresentation.STANDARD))
        fromRegistries(uuidRegistry, customRegistry, defaultRegistry)
    }

    private fun getEnv(name: String): String {
        return System.getenv(name)
            ?: throw EiffelFlowException.DataBaseException("$name not found")
    }

    companion object {
        private const val DATABASE_NAME = "DATABASE_NAME"
    }
}