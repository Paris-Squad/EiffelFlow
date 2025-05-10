package org.example.data.remote.mapper

import kotlinx.datetime.LocalDateTime
import org.example.data.remote.dto.MongoTaskDto
import org.example.domain.model.RoleType
import org.example.domain.model.Task
import org.example.domain.model.TaskState
import java.util.UUID

class TaskMapper : Mapper<MongoTaskDto, Task> {

    override fun toDto(entity: Task) = MongoTaskDto(
        _id = entity.taskId.toString(),
        title = entity.title,
        description = entity.description,
        createdAt = entity.createdAt.toString(),
        creatorId = entity.creatorId.toString(),
        projectId = entity.projectId.toString(),
        assignedId = entity.assignedId.toString(),
        stateId = entity.state.stateId.toString(),
        stateName = entity.state.name,
        role = entity.role.name
    )

    override fun fromDto(dto: MongoTaskDto) = Task(
        taskId = UUID.fromString(dto._id),
        title = dto.title,
        description = dto.description,
        createdAt = LocalDateTime.parse(dto.createdAt),
        creatorId = UUID.fromString(dto.creatorId),
        projectId = UUID.fromString(dto.projectId),
        assignedId = UUID.fromString(dto.assignedId),
        state = TaskState(
            stateId = UUID.fromString(dto.stateId),
            name = dto.stateName
        ),
        role = RoleType.valueOf(dto.role)
    )
}

