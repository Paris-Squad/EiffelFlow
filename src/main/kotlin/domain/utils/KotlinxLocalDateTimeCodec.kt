package org.example.domain.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.codecs.jsr310.LocalDateTimeCodec
import java.time.Instant
import java.time.ZoneOffset

class KotlinxLocalDateTimeCodec : Codec<LocalDateTime> {

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): LocalDateTime {
        val dateTime = reader.readDateTime()
        val javaTime = Instant.ofEpochMilli(dateTime)
            .atZone(ZoneOffset.UTC)
            .toLocalDateTime()
        LocalDateTimeCodec()
        return javaTime.toKotlinLocalDateTime()
    }

    override fun encode(writer: BsonWriter, value: LocalDateTime, encoderContext: EncoderContext) {
        val millis = value.toJavaLocalDateTime()
            .atZone(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
        writer.writeDateTime(millis)
    }

    override fun getEncoderClass(): Class<LocalDateTime> = LocalDateTime::class.java
}