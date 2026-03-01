package tv.moplayer.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import tv.moplayer.core.database.entity.HistoryEntity

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_entities ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: HistoryEntity)

    @Query("DELETE FROM history_entities WHERE contentId = :contentId")
    suspend fun delete(contentId: String)
}