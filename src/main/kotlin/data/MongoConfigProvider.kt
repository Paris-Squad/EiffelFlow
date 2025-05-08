package org.example.data

import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.bson.UuidRepresentation
import org.bson.codecs.UuidCodec
import org.bson.codecs.configuration.CodecRegistries

class MongoConfigProvider {
    fun getDatabase(): MongoDatabase {
        val codecRegistry = CodecRegistries.fromRegistries(
            CodecRegistries.fromCodecs(UuidCodec(UuidRepresentation.STANDARD)),
            MongoClientSettings.getDefaultCodecRegistry()
        )
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