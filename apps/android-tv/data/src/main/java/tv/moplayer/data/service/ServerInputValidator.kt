package tv.moplayer.data.service

import tv.moplayer.domain.model.ServerProfile
import tv.moplayer.domain.model.ServerType

object ServerInputValidator {
    fun validate(profile: ServerProfile): Result<Unit> {
        if (profile.baseUrl.isBlank()) return Result.failure(IllegalArgumentException("URL is required"))
        if (!profile.baseUrl.startsWith("http") && profile.type != ServerType.M3U_FILE) {
            return Result.failure(IllegalArgumentException("URL must start with http/https"))
        }
        if (profile.type == ServerType.XTREAM && (profile.username.isNullOrBlank() || profile.encryptedPassword.isNullOrBlank())) {
            return Result.failure(IllegalArgumentException("Xtream credentials are required"))
        }
        return Result.success(Unit)
    }
}