package org.example.data.remote

import com.mongodb.kotlin.client.coroutine.MongoClient
import org.example.domain.exception.EiffelFlowException

class MongoClientProvider {
    val client: MongoClient by lazy {
        MongoClient.create(getConnectionString())
    }

    private fun getConnectionString(): String {
        val protocol = getEnv(PROTOCOL)
        val userName = getEnv(MONGO_USER_NAME)
        val password = getEnv(MONGO_PASSWORD)
        val hostName = getEnv(MONGO_HOSTNAME)
        val connectionOptions = getEnv(MONGO_CONNECTION_OPTIONS)
        val appName = getEnv(MONGO_APP_NAME)
        val connectionString = "$protocol://$userName:$password@$hostName/$connectionOptions&appName=$appName"
        return connectionString
    }

    private fun getEnv(name: String): String {
        return System.getenv(name)
            ?: throw EiffelFlowException.DataBaseException("$name not found")
    }

    companion object {
        private const val PROTOCOL = "MONGO_PROTOCOL"
        private const val MONGO_USER_NAME = "MONGO_USER_NAME"
        private const val MONGO_PASSWORD = "MONGO_PASSWORD"
        private const val MONGO_HOSTNAME = "MONGO_HOSTNAME"
        private const val MONGO_CONNECTION_OPTIONS = "MONGO_CONNECTION_OPTIONS"
        private const val MONGO_APP_NAME = "MONGO_APP_NAME"
    }
}