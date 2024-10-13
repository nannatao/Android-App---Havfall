package no.uio.ifi.in2000.team27.havapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import no.uio.ifi.in2000.team27.havapp.model.AppEntity
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.DATABASE_NAME
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.DATABASE_VERSION
import no.uio.ifi.in2000.team27.havapp.model.cleaning.CleaningActivity
import no.uio.ifi.in2000.team27.havapp.model.user.User

/*
Her opprettes databasen til applikasjonen naar den kjores.
 */

@TypeConverters(Converters::class)
@Database(entities = [User::class, CleaningActivity::class, AppEntity::class], version = DATABASE_VERSION)
abstract class HavfallDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun cleaningActivityDao(): CleaningActivityDao
    abstract fun appStateDao(): AppStateDao

    companion object {
        @Volatile // Ensures a single instance
        private var INSTANCE: HavfallDatabase? = null

        fun getDatabase(context: Context): HavfallDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = databaseBuilder(
                    context.applicationContext,
                    HavfallDatabase::class.java,
                    DATABASE_NAME
                ).addTypeConverter(Converters())
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // Egentlig ikke lurt, kan stoppe main-tråden med UI, men fungerer fint nå
                    .build()                  // ... Dersom det oppstår problemer, kan vi refaktorere.
                INSTANCE = instance
                instance
            }


        }
    }
}