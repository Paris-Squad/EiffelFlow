package presentation.presenter.auth

import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.auth.LogoutUseCase
import org.example.presentation.presenter.auth.LogoutPresenter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LogoutPresenterTest {
    private val logoutUseCase : LogoutUseCase = mockk(relaxed = true)
    private lateinit var logoutPresenter: LogoutPresenter

    @BeforeEach
    fun setup(){
        logoutPresenter = LogoutPresenter(logoutUseCase)
    }

    @Test
    fun `should return success when logout is successful`() {
        // Given
        coEvery { logoutUseCase.logout() } just runs

        // When
        val result = logoutPresenter.logout()

        // Then
        Truth.assertThat(result).isEqualTo("Logout successful")
    }

    @Test
    fun `should return failure when logout fails with AuthorizationException message`() {
        // Given
        val exception = EiffelFlowException.AuthorizationException("failed logout")
        coEvery { logoutUseCase.logout() } throws exception

        // When
        val result = logoutPresenter.logout()

        // Then
        Truth.assertThat(result).isEqualTo("failed logout")
    }

    @Test
    fun `should return failure message when logout fails with null message`() {
        // Given
        val exception = EiffelFlowException.AuthorizationException(null)
        coEvery { logoutUseCase.logout() } throws exception

        // When
        val result = logoutPresenter.logout()

        // Then
        Truth.assertThat(result).isEqualTo("Logout failed")
    }


}