package no.uio.ifi.in2000.team27.havapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppEntity(
    @PrimaryKey val userId: Long = 0, // Samme som User.uid
    @ColumnInfo(name = "hasCompletedOnboarding") val hasCompletedOnboarding: Boolean = false,
)
