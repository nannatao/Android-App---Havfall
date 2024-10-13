package no.uio.ifi.in2000.team27.havapp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import no.uio.ifi.in2000.team27.havapp.model.user.User

/*********************************
 *  User Database Access Object  *
 *********************************/
/**
 * Interface for å aksessere bruker-objecter i databasen.
 * Tilbyr metoder for å hente, oppdatere og slette brukere.
 * Brukes av Room til å generere implementasjon i Java med SQL.
 */

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>      // Burde ikke brukes av appen

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE username LIKE :username LIMIT 1")
    fun findByUsername(username: String): User

    @Query("SELECT * FROM user WHERE uid = :userId")
    fun getUserById(userId: Long): User

    @Delete
    fun delete(user: User)

    @Insert
    fun insert(user: User): Long

    @Query("UPDATE user SET username = :username WHERE uid = :userId")
    fun updateUsername(userId: Long, username: String)

    @Query("UPDATE user SET avatarId = :avatarId WHERE uid = :userId")
    fun updateAvatarId(userId: Long, avatarId: Int)

    @Query("DELETE FROM user")
    fun deleteAll(): Int
}
