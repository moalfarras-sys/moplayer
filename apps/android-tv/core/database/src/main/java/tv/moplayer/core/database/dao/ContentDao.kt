package tv.moplayer.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import tv.moplayer.core.database.entity.ContentEntity

@Dao
interface ContentDao {
    @Query("SELECT * FROM content_entities WHERE type = :type ORDER BY title ASC")
    fun observeByType(type: String): Flow<List<ContentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ContentEntity>)

    @Query("DELETE FROM content_entities")
    suspend fun clearAll()
}