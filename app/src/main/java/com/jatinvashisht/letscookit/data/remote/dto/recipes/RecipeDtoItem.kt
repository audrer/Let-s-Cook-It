package com.jatinvashisht.letscookit.data.remote.dto.recipes

data class RecipeDtoItem(
    val imageUrl: String = "",
    val ingredient: List<Ingredient> = listOf(),
    val method: List<String> = listOf(),
    val tag: String = "",
    val title: String = ""
)