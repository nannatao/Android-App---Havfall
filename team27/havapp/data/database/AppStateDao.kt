package no.uio.ifi.in2000.team27.havapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import no.uio.ifi.in2000.team27.havapp.model.AppEntity

/**************************************
 *  App State Database Access Object  *
 **************************************/
/**
 * Interface for å aksessere app-tilstand i databasen.
 * Tilbyr metoder for å sette inn, hente og slette tilstandsdata.
 * Med tilstandsdata menes data som er relevant for hele appen, og ikke bare en spesifikk bruker.
 */
@Dao
interface AppStateDao {
    @Insert
    fun insert(appEntity: AppEntity)
    @Query("SELECT hasCompletedOnboarding FROM AppEntity LIMIT 1")
    fun getHasCompletedOnboarding(): Boolean
    @Query("SELECT userId FROM AppEntity LIMIT 1")
    fun getUserId(): Long
    @Query("DELETE FROM AppEntity")
    fun deleteAll(): Int
}