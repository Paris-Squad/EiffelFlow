package di

import io.mockk.mockk
import org.example.di.useCasesModule
import org.example.domain.repository.UserRepository
import org.example.domain.usecase.auth.HashPasswordUseCase
import org.example.domain.usecase.auth.RegisterUseCase
import org.example.domain.usecase.auth.ValidatePasswordUseCase
import org.example.domain.usecase.auth.ValidateUserNameUseCase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get

class UseCasesModuleTest: KoinTest {

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single<UserRepository> { mockk(relaxed = true) }
                }, useCasesModule
            )
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `validatePasswordUseCase can be resolved`() {
        val useCase = get<ValidatePasswordUseCase>()
        assertNotNull(useCase)
    }

    @Test
    fun `validateUserNameUseCase can be resolved`() {
        val useCase = get<ValidateUserNameUseCase>()
        assertNotNull(useCase)
    }

    @Test
    fun `hashPasswordUseCase can be resolved`() {
        val useCase = get<HashPasswordUseCase>()
        assertNotNull(useCase)
    }

    @Test
    fun `registerUseCase can be resolved`() {
        val useCase = get<RegisterUseCase>()
        assertNotNull(useCase)
        assertTrue(true, "RegisterUseCase was successfully resolved")
    }
}