package tv.moplayer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import tv.moplayer.core.database.dao.ContentDao
import tv.moplayer.core.database.dao.FavoriteDao
import tv.moplayer.core.database.dao.HistoryDao
import tv.moplayer.core.database.dao.ServerDao
import tv.moplayer.core.database.entity.ContentEntity
import tv.moplayer.core.database.entity.DownloadCacheIndexEntity
import tv.moplayer.core.database.entity.EpgEntity
import tv.moplayer.core.database.entity.FavoriteEntity
import tv.moplayer.core.database.entity.HistoryEntity
import tv.moplayer.core.database.entity.ServerEntity
import tv.moplayer.core.database.entity.SyncStateEntity

@Database(
    entities = [
        ServerEntity::class,
        ContentEntity::class,
        EpgEntity::class,
        HistoryEntity::class,
        FavoriteEntity::class,
        DownloadCacheIndexEntity::class,
        SyncStateEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MoPlayerDatabase : RoomDatabase() {
    abstract fun serverDao(): ServerDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun historyDao(): HistoryDao
    abstract fun contentDao(): ContentDao
}
