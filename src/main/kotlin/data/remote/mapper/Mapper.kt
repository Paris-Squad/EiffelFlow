package org.example.data.remote.mapper

interface Mapper<Dto, Entity> {
    fun toDto(entity: Entity): Dto
    fun fromDto(dto: Dto): Entity
}

