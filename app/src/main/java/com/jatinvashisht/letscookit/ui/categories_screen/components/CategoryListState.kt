package com.jatinvashisht.letscookit.ui.categories_screen.components

import com.jatinvashisht.letscookit.data.remote.dto.categories.CategoryDtoItem

data class CategoryListState(
    val isLoading: Boolean = false,
    val categories: List<CategoryDtoItem> = emptyList(),
    val error: String = "",
)
