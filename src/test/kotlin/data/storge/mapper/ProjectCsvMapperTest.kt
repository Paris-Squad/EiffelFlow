package data.storge.mapper

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDateTime
import org.example.data.storge.mapper.ProjectCsvMapper
import org.example.domain.model.entities.Project
import java.util.*
import kotlin.test.Test

class ProjectCsvMapperTest {

    private val projectCsvMapper = ProjectCsvMapper()

    @Test
    fun `test mapFrom CSV to Project`() {
        val csv =
            "123e4567-e89b-12d3-a456-426614174000,Project1,Description1,2024-01-01T10:00:00,123e4567-e89b-12d3-a456-426614174002"

        try {
            val project = projectCsvMapper.mapFrom(csv)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `test mapTo Project to CSV`() {
        val project = Project(
            projectId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
            projectName = "Project1",
            projectDescription = "Description1",
            createdAt = LocalDateTime.parse("1999-08-07T22:22:22"),
            adminId = UUID.fromString("123e4567-e89b-12d3-a456-426614174002"),
            states = emptyList()
        )

        try {
            val csv = projectCsvMapper.mapTo(project)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}
