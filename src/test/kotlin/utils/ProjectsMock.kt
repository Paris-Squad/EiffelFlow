package utils

import kotlinx.datetime.LocalDateTime
import org.example.domain.model.Project
import org.example.domain.model.TaskState
import java.util.UUID

object ProjectsMock {
    val CORRECT_PROJECT = Project(
        projectId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748"),
        projectName = "Project1",
        projectDescription = "Description1",
        createdAt = LocalDateTime.parse("1999-08-07T22:22:22"),
        adminId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5741"),
        taskStates = listOf(
            TaskState(
                stateId = UUID.fromString("8d4f05a4-5717-4562-b3fc-2c963f66afa7"),
                name = "Backlog"
            ),
            TaskState(
                stateId = UUID.fromString("9e4f05a4-5717-4562-b3fc-2c963f66afa8"),
                name = "In Progress"
            )
        )
    )

    val updatedProject = CORRECT_PROJECT.copy(projectDescription = "UpdatedProject")

    const val CORRECT_CSV_STRING_LINE =
        "02ad4499-5d4c-4450-8fd1-8294f1bb5748,Project1,Description1,1999-08-07T22:22:22,02ad4499-5d4c-4450-8fd1-8294f1bb5741,[8d4f05a4-5717-4562-b3fc-2c963f66afa7,Backlog;9e4f05a4-5717-4562-b3fc-2c963f66afa8,In Progress]"

    const val CORRECT_CSV_STRING_LINE_WITH_EMPTY_STATES =
        "02ad4499-5d4c-4450-8fd1-8294f1bb5748,Project1,Description1,1999-08-07T22:22:22,02ad4499-5d4c-4450-8fd1-8294f1bb5741,"

    const val UPDATED_PROJECT_CSV = "id1,Project1,UpdatedProject,1999-08-07T22:22:22,admin-id,..."
}