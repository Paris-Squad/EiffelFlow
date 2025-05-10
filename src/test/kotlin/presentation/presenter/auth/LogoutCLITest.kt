package presentation.presenter.auth

import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.auth.LogoutUseCase
import org.example.presentation.auth.LogoutCLI
import org.example.presentation.io.Printer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LogoutCLITest {
    private val logoutUseCase : LogoutUseCase = mockk(relaxed = true)
    private lateinit var logoutPresenter: LogoutCLI
    private val printer: Printer = mockk(relaxed = true)

    @BeforeEach
    fun setup(){
        logoutPresenter = LogoutCLI(logoutUseCase = logoutUseCase, printer = printer)
    }

    @Test
    fun `should print success message when logout success`() {
        // Given
        coEvery { logoutUseCase.logout() } just runs

        // When
         logoutPresenter.start()

        // Then
        verify { printer.displayLn("Logout successful") }
    }

    @Test
    fun `should print failure message when logout fail`() {
        // Given
        val exception = EiffelFlowException.AuthorizationException("failed logout")
        coEvery { logoutUseCase.logout() } throws exception

        // When
        logoutPresenter.start()

        // Then
        verify { printer.displayLn("Logout failed") }
    }

}