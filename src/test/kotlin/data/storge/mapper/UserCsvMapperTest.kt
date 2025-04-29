package data.storge.mapper

import com.google.common.truth.Truth.assertThat
import org.example.data.storge.mapper.UserCsvMapper
import org.example.domain.model.entities.RoleType
import org.example.domain.model.entities.User
import java.util.*
import kotlin.test.Test

class UserCsvMapperTest {

    private val userCsvMapper = UserCsvMapper()

    @Test
    fun `should map CSV line to User entity correctly`() {
        try {
            val result = userCsvMapper.mapFrom(CSV_STRING_LINE)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should map User entity to CSV line correctly`() {
        try {
            val result = userCsvMapper.mapTo(USER)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    companion object {
        private val USER = User(
            userId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748"),
            username = "username",
            password = "password",
            role = RoleType.ADMIN
        )

        private const val CSV_STRING_LINE = "02ad4499-5d4c-4450-8fd1-8294f1bb5748,username,password,ADMIN"
    }
}
