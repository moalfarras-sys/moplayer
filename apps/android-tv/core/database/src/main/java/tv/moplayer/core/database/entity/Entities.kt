package tv.moplayer.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "server_entities")
data class ServerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val baseUrl: String,
    val username: String?,
    val encryptedPassword: String?,
    val externalEpgUrl: String?,
    val isDefault: Boolean
)

@Entity(tableName = "content_entities")
data class ContentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String,
    val groupName: String?,
    val posterUrl: String?,
    val streamUrl: String?
)

@Entity(tableName = "epg_entities")
data class EpgEntity(
    @PrimaryKey val id: String,
    val channelId: String,
    val title: String,
    val startUtcMillis: Long,
    val endUtcMillis: Long,
    val description: String?
)

@Entity(tableName = "history_entities")
data class HistoryEntity(
    @PrimaryKey val contentId: String,
    val positionMs: Long,
    val durationMs: Long,
    val updatedAt: Long
)

@Entity(tableName = "favorite_entities")
data class FavoriteEntity(
    @PrimaryKey val contentId: String,
    val createdAt: Long
)

@Entity(tableName = "download_cache_index")
data class DownloadCacheIndexEntity(
    @PrimaryKey val key: String,
    val expiresAt: Long
)

@Entity(tableName = "sync_state")
data class SyncStateEntity(
    @PrimaryKey val id: String,
    val lastSyncAt: Long,
    val status: String
)