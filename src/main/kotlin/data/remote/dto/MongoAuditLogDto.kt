package org.example.data.remote.dto

import org.bson.codecs.pojo.annotations.BsonId

data class MongoAuditLogDto(
    @BsonId val _id: String,
    val itemId: String,
    val itemName: String,
    val userId: String,
    val editorName: String,
    val actionType: String,
    val auditTime: String,
    val changedField: String?,
    val oldValue: String?,
    val newValue: String?
)
