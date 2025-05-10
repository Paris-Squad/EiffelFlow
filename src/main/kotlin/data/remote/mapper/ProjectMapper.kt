package org.example.data.remote.mapper

import kotlinx.datetime.LocalDateTime
import org.example.data.remote.dto.MongoProjectDto
import org.example.data.remote.dto.MongoTaskStateDto
import org.example.domain.model.Project
import org.example.domain.model.TaskState
import java.util.UUID

class ProjectMapper : Mapper<MongoProjectDto, Project> {

    override fun toDto(entity: Project) = MongoProjectDto(
        _id = entity.projectId.toString(),
        projectName = entity.projectName,
        projectDescription = entity.projectDescription,
        createdAt = entity.createdAt.toString(),
        adminId = entity.adminId.toString(),
        taskStates = entity.taskStates.map {
            MongoTaskStateDto(
                stateId = it.stateId.toString(),
                name = it.name
            )
        }
    )

    override fun fromDto(dto: MongoProjectDto) = Project(
        projectId = UUID.fromString(dto._id),
        projectName = dto.projectName,
        projectDescription = dto.projectDescription,
        createdAt = LocalDateTime.parse(dto.createdAt),
        adminId = UUID.fromString(dto.adminId),
        taskStates = dto.taskStates.map {
            TaskState(
                stateId = UUID.fromString(it.stateId),
                name = it.name
            )
        }
    )
}
