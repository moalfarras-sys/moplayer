package tv.moplayer.data.service

import tv.moplayer.domain.model.ServerProfile
import tv.moplayer.domain.model.ServerType

object ServerInputValidator {
    fun validate(profile: ServerProfile): Result<Unit> {
        if (profile.baseUrl.isBlank()) return Result.failure(IllegalArgumentException("URL is required"))
        val isHttp = profile.baseUrl.startsWith("http://") || profile.baseUrl.startsWith("https://")
        val isRawM3u = profile.baseUrl.contains("#EXTM3U", ignoreCase = true)
        if (!isHttp && !isRawM3u && profile.type == ServerType.M3U_URL) {
            return Result.failure(IllegalArgumentException("M3U URL must start with http/https"))
        }
        if (profile.type == ServerType.XTREAM && (profile.username.isNullOrBlank() || profile.encryptedPassword.isNullOrBlank())) {
            return Result.failure(IllegalArgumentException("Xtream credentials are required"))
        }
        return Result.success(Unit)
    }
}
