package org.example.data.remote.dto

import org.bson.codecs.pojo.annotations.BsonId

data class MongoTaskDto(
    @BsonId val _id: String,
    val title: String,
    val description: String,
    val createdAt: String,
    val creatorId: String,
    val projectId: String,
    val assignedId: String,
    val stateId: String,
    val stateName: String,
    val role: String
)
