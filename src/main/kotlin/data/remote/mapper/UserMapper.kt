package org.example.data.remote.mapper

import org.example.data.remote.dto.MongoUserDto
import org.example.domain.model.RoleType
import org.example.domain.model.User
import java.util.UUID

class UserMapper : Mapper<MongoUserDto, User> {

    override fun toDto(entity: User) = MongoUserDto(
        _id = entity.userId.toString(),
        username = entity.username,
        password = entity.password,
        role = entity.role.name
    )

    override fun fromDto(dto: MongoUserDto) = User(
        userId = UUID.fromString(dto._id),
        username = dto.username,
        password = dto.password,
        role = RoleType.valueOf(dto.role)
    )
}
