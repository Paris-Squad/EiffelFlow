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

class DataBaseConfigs {

    fun getMongoClient(): MongoClient = MongoClient.create(getConnectionString())

    fun getDatabase(mongoClient: MongoClient): MongoDatabase {
        val defaultRegistry = MongoClientSettings.getDefaultCodecRegistry()
        val customRegistry = fromCodecs(KotlinxLocalDateTimeCodec())
        val uuidRegistry = fromCodecs(UuidCodec(UuidRepresentation.STANDARD))
        val codecRegistry = fromRegistries(uuidRegistry, customRegistry, defaultRegistry)
        val databaseName = System.getenv(DATABASE_NAME)
            ?: throw EiffelFlowException.DataBaseException("$DATABASE_NAME not found")
        return mongoClient.getDatabase(databaseName).withCodecRegistry(codecRegistry)
    }

    private fun getConnectionString(): String {
        val protocol = System.getenv(PROTOCOL) ?: throw EiffelFlowException.DataBaseException("$PROTOCOL not found")
        val userName =
            System.getenv(MONGO_USER_NAME) ?: throw EiffelFlowException.DataBaseException("$MONGO_USER_NAME not found")
        val password =
            System.getenv(MONGO_PASSWORD) ?: throw EiffelFlowException.DataBaseException("$MONGO_PASSWORD not found")
        val hostName =
            System.getenv(MONGO_HOSTNAME) ?: throw EiffelFlowException.DataBaseException("$MONGO_HOSTNAME not found")
        val connectionOptions =
            System.getenv(MONGO_CONNECTION_OPTIONS)
                ?: throw EiffelFlowException.DataBaseException("$MONGO_CONNECTION_OPTIONS not found")
        val appName =
            System.getenv(MONGO_APP_NAME) ?: throw EiffelFlowException.DataBaseException("$MONGO_APP_NAME not found")

        val connectionString = "$protocol://$userName:$password@$hostName/$connectionOptions&appName=$appName"
        return connectionString
    }

    companion object {
        private const val PROTOCOL = "MONGO_PROTOCOL"
        private const val DATABASE_NAME = "DATABASE_NAME"
        private const val MONGO_USER_NAME = "MONGO_USER_NAME"
        private const val MONGO_PASSWORD = "MONGO_PASSWORD"
        private const val MONGO_HOSTNAME = "MONGO_HOSTNAME"
        private const val MONGO_CONNECTION_OPTIONS = "MONGO_CONNECTION_OPTIONS"
        private const val MONGO_APP_NAME = "MONGO_APP_NAME"
    }
}