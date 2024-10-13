package no.uio.ifi.in2000.team27.havapp.data.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Funksjoner for konvertering mellom GeoJson og trashmap.
 */

@ProvidedTypeConverter
class Converters {
    private val gson = Gson()
    @TypeConverter
    fun trashMapToJson(trashMap: Map<String, Int>?): String? {
        return gson.toJson(trashMap)
    }

    @TypeConverter
    fun jsonToTrashMap(json: String?): Map<String, Int>? {
        val mapType = object : TypeToken<Map<String, Int>>() {}.type
        return gson.fromJson(json, mapType)
    }
}