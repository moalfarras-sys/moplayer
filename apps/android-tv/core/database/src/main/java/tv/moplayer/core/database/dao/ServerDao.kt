package tv.moplayer.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import tv.moplayer.core.database.entity.ServerEntity

@Dao
interface ServerDao {
    @Query("SELECT * FROM server_entities ORDER BY name ASC")
    fun observeAll(): Flow<List<ServerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(server: ServerEntity)

    @Query("DELETE FROM server_entities WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE server_entities SET isDefault = CASE WHEN id = :id THEN 1 ELSE 0 END")
    suspend fun setDefault(id: String)
}