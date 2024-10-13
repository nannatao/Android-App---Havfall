package no.uio.ifi.in2000.team27.havapp.model.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/********************
 * User Data Entity *
 ********************/

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "avatarId") val avatarId: Int?,
    @ColumnInfo(name = "lastLocation") val lastLocation: String?,
    @ColumnInfo(name = "hasCompletedOnboarding") val hasCompletedOnboarding: Boolean = false,
)
