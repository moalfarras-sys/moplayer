package tv.moplayer.data

import org.junit.Assert.assertTrue
import org.junit.Test
import tv.moplayer.data.service.ServerInputValidator
import tv.moplayer.domain.model.ServerProfile
import tv.moplayer.domain.model.ServerType

class ServerInputValidatorTest {
    @Test
    fun xtream_requires_credentials() {
        val profile = ServerProfile(
            id = java.util.UUID.randomUUID().toString(),
            name = "x",
            type = ServerType.XTREAM,
            baseUrl = "https://example.com"
        )

        val result = ServerInputValidator.validate(profile)
        assertTrue(result.isFailure)
    }
}