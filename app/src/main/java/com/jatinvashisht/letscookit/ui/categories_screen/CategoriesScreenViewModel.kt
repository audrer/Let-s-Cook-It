package com.jatinvashisht.letscookit.ui.categories_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jatinvashisht.letscookit.core.Resource
import com.jatinvashisht.letscookit.domain.repository.RecipeRepository
import com.jatinvashisht.letscookit.ui.categories_screen.components.CategoryListState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class CategoriesScreenViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
) : ViewModel() {

    private val _categoryListState = mutableStateOf<CategoryListState>(CategoryListState())
    val categoryListState: State<CategoryListState> = _categoryListState

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            recipeRepository.getCategories().collectLatest {categoryDtoItem ->
                when(categoryDtoItem){
                    is Resource.Error -> {
                        _categoryListState.value = _categoryListState.value.copy(isLoading = false, error = categoryDtoItem.error?:"unable to load categories")
                    }
                    is Resource.Loading -> {
                        _categoryListState.value = _categoryListState.value.copy(isLoading = true, error = "")
                    }
                    is Resource.Success -> {
                        _categoryListState.value = _categoryListState.value.copy(isLoading =  false, categories = categoryDtoItem.data?: emptyList(), error = "")
                    }
                }
            }
        }
    }
}