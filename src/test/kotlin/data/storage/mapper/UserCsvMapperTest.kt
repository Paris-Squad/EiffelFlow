package data.storage.mapper

import com.google.common.truth.Truth.assertThat
import org.example.data.storage.mapper.UserCsvMapper
import org.example.domain.model.entities.RoleType
import org.example.domain.model.entities.User
import java.util.*
import kotlin.test.Test

class UserCsvMapperTest {

    private val userCsvMapper = UserCsvMapper()

    @Test
    fun `should map CSV line to User entity correctly`() {

        //Given / When
        val result = userCsvMapper.mapFrom(CSV_STRING_LINE)

        // Then
        assertThat(result).isEqualTo(USER)

    }

    @Test
    fun `should map User entity to CSV line correctly`() {

        //Given / When
        val result = userCsvMapper.mapTo(USER)

        // Then
        assertThat(result).isEqualTo(CSV_STRING_LINE)
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
