package com.jatinvashisht.letscookit.ui.home_screen.components

import com.jatinvashisht.letscookit.data.remote.dto.categories.CategoryDtoItem

data class ComponentCategoriesState(
    val isLoading: Boolean = true,
    val categories: List<CategoryDtoItem> = emptyList(),
    val error: String = "",
)
