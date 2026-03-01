package tv.moplayer.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import tv.moplayer.core.database.MoPlayerDatabase
import tv.moplayer.core.database.entity.ContentEntity
import tv.moplayer.core.database.entity.HistoryEntity
import tv.moplayer.core.database.entity.ServerEntity
import tv.moplayer.domain.model.ContentItem
import tv.moplayer.domain.model.ContentType
import tv.moplayer.domain.model.ServerProfile
import tv.moplayer.domain.model.ServerType

class LocalStorageRepository(
    private val db: MoPlayerDatabase,
    private val cipher: CredentialsCipher = CredentialsCipher()
) {
    fun observeServers(): Flow<List<ServerProfile>> = db.serverDao().observeAll().map { list ->
        list.map { entity ->
            val decryptedPassword = entity.encryptedPassword?.let {
                runCatching { cipher.decrypt(it) }.getOrNull()
            }
            ServerProfile(
                id = entity.id,
                name = entity.name,
                type = ServerType.valueOf(entity.type),
                baseUrl = entity.baseUrl,
                username = entity.username,
                encryptedPassword = decryptedPassword,
                externalEpgUrl = entity.externalEpgUrl,
                isDefault = entity.isDefault
            )
        }
    }

    suspend fun saveServer(server: ServerProfile) {
        val encrypted = server.encryptedPassword?.let { cipher.encrypt(it) }
        db.serverDao().upsert(
            ServerEntity(
                id = server.id,
                name = server.name,
                type = server.type.name,
                baseUrl = server.baseUrl,
                username = server.username,
                encryptedPassword = encrypted,
                externalEpgUrl = server.externalEpgUrl,
                isDefault = server.isDefault
            )
        )
    }

    suspend fun deleteServer(id: String) {
        db.serverDao().deleteById(id)
    }

    suspend fun setDefaultServer(id: String) {
        db.serverDao().setDefault(id)
    }

    suspend fun replaceCatalog(items: List<ContentItem>) {
        db.contentDao().clearAll()
        db.contentDao().upsertAll(items.map {
            ContentEntity(
                id = it.id,
                title = it.title,
                type = it.type.name,
                groupName = it.group,
                posterUrl = it.posterUrl,
                streamUrl = it.streamUrl
            )
        })
    }

    suspend fun addFavorite(item: ContentItem) {
        db.favoriteDao().upsert(
            tv.moplayer.core.database.entity.FavoriteEntity(
                contentId = item.id,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun addHistory(item: ContentItem, positionMs: Long = 0L, durationMs: Long = 0L) {
        db.historyDao().upsert(
            HistoryEntity(
                contentId = item.id,
                positionMs = positionMs,
                durationMs = durationMs,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    fun observeMovies(): Flow<List<ContentItem>> = db.contentDao().observeByType(ContentType.MOVIE.name).map { rows ->
        rows.map { row ->
            ContentItem(
                id = row.id,
                title = row.title,
                type = ContentType.MOVIE,
                posterUrl = row.posterUrl,
                group = row.groupName,
                streamUrl = row.streamUrl
            )
        }
    }

    fun observeSeries(): Flow<List<ContentItem>> = db.contentDao().observeByType(ContentType.SERIES.name).map { rows ->
        rows.map { row ->
            ContentItem(
                id = row.id,
                title = row.title,
                type = ContentType.SERIES,
                posterUrl = row.posterUrl,
                group = row.groupName,
                streamUrl = row.streamUrl
            )
        }
    }
}
