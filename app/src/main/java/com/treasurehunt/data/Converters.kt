package com.treasurehunt.data

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> = Json.decodeFromString(value)

    @TypeConverter
    fun listToString(value: List<String>): String = Json.encodeToString(value)
}