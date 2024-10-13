package no.uio.ifi.in2000.team27.havapp.model.cleaning

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import no.uio.ifi.in2000.team27.havapp.data.database.Converters


// enum klasse for typer av soppel
enum class TrashType {
    FISKEUTSTYR,
    PLAST,
    SIGARETTER,
    ANNET
}

// funksjon som konverterer string til TrashType
fun stringToTrashType(string: String): TrashType {
    return when (string) {
        "Fiskeutstyr" -> TrashType.FISKEUTSTYR
        "Plast" -> TrashType.PLAST
        "Sigaretter" -> TrashType.SIGARETTER
        "Annet" -> TrashType.ANNET
        else -> throw IllegalArgumentException("Invalid TrashType (String): $string")
    }
}


// dataklasse for lagring i HavfallDatabase
@Entity
data class CleaningActivity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "duration") val duration: String,
    @TypeConverters(Converters::class)
    @ColumnInfo(name = "trash") val trash: Map<String, Int>,
    @ColumnInfo(name = "user_id") val userId: Long // Fremmednøkkel til bruker
)