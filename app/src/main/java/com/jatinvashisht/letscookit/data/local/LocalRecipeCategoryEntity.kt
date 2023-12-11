package com.jatinvashisht.letscookit.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class LocalRecipeCategoryEntity(
    val category: String = "",
    val imageUrl: String = "",
    @PrimaryKey(autoGenerate = true)
    val primaryKey: Long? = null
)
