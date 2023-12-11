package com.jatinvashisht.letscookit.ui.home_screen.components

import com.jatinvashisht.letscookit.data.remote.dto.recipes.RecipeDtoItem

data class ComponentTopRecipesState(
    val recipes: List<RecipeDtoItem> = emptyList(),
    val error: String = "",
    val loading: Boolean = true
)
