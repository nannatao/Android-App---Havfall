package no.uio.ifi.in2000.team27.havapp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import no.uio.ifi.in2000.team27.havapp.model.cleaning.CleaningActivity

/**
 * Interface for å oppdatere ryddeaktiviteter i databasen.
 * Tilbyr metoder for å sette inn, hente og slette ryddeaktiviteter.
 */
@Dao
interface CleaningActivityDao {

    @Insert//(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cleaningActivity: CleaningActivity): Long

    @Update
    suspend fun update(cleaningActivity: CleaningActivity)

    @Delete
    suspend fun delete(cleaningActivity: CleaningActivity)

    @Query("SELECT * FROM CleaningActivity WHERE user_id = :userId")
    fun getCleaningActivitiesForUser(userId: Long): Flow<List<CleaningActivity>>

    @Query("SELECT * FROM CleaningActivity WHERE id = :id")
    suspend fun getCleaningActivityById(id: Long): CleaningActivity

    @Update
    suspend fun updateCleaningActivityById(id: Long, cleaningActivity: CleaningActivity) {
        val oldCleaningActivity = getCleaningActivityById(id)
    }
}