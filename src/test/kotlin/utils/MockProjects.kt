package utils

import kotlinx.datetime.LocalDateTime
import org.example.domain.model.entities.Project
import org.example.domain.model.entities.State
import java.util.UUID

object MockProjects {
    val CORRECT_PROJECT = Project(
        projectId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748"),
        projectName = "Project1",
        projectDescription = "Description1",
        createdAt = LocalDateTime.parse("1999-08-07T22:22:22"),
        adminId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5741"),
        states = listOf(
            State(
                stateId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5749"),
                name = "In Progress"
            ),
            State(
                stateId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5749"),
                name = "In Progress"
            ),
            State(
                stateId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5749"),
                name = "In Progress"
            ),
            State(
                stateId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5749"),
                name = "In Progress"
            )
        )
    )

    const val CORRECT_CSV_STRING_LINE =
        "02ad4499-5d4c-4450-8fd1-8294f1bb5748,Project1,Description1,1999-08-07T22:22:22,02ad4499-5d4c-4450-8fd1-8294f1bb5741"
}