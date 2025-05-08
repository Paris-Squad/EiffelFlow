package org.example.data

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

        return MongoClient.create(CONNECTION_STRING_URI_PLACEHOLDER)
            .getDatabase(DATABASE_NAME)
            .withCodecRegistry(codecRegistry)
    }

    companion object {
        private const val CONNECTION_STRING_URI_PLACEHOLDER =
            "mongodb+srv://abdelrahmanraafaat:5PcgrHNqtPP4Cdez@cluster0.bekcque.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
        private const val DATABASE_NAME = "eiffle_flow"
    }
}