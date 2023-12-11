package com.jatinvashisht.letscookit.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson

class MethodConverter {
    @TypeConverter
    fun fromListToJson(value: List<String>): String = Gson().toJson(value)

    @TypeConverter
    fun fromJsonToList(value: String): List<String> = Gson().fromJson(value, Array<String>::class.java).toList()
}