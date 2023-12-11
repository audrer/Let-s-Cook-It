package com.jatinvashisht.letscookit.ui.recipe_screen.components

import com.jatinvashisht.letscookit.data.remote.dto.recipes.RecipeDtoItem

data class RecipeScreenState(
    val recipe: RecipeDtoItem = RecipeDtoItem(),
    val isLoading: Boolean = true,
    val error: String = "",
)
