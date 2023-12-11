package com.jatinvashisht.letscookit.ui.recipe_list_screen

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jatinvashisht.letscookit.core.Constants
import com.jatinvashisht.letscookit.data.remote.dto.recipes.RecipeDtoItem
import com.jatinvashisht.letscookit.domain.pagination.RecipePaginator
import com.jatinvashisht.letscookit.domain.repository.RecipeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var state by mutableStateOf<RecipeListScreenState>(RecipeListScreenState())
    val category = mutableStateOf<String>("")
    val imageUrl = mutableStateOf<String>("")
    val isEditModeOn = mutableStateOf<Boolean>(false)
    val getSavedRecipes = mutableStateOf<Boolean>(false)
    private val _searchBoxState = mutableStateOf("")
    val searchBoxState: State<String> = _searchBoxState
    var searchJob: Job? = null
    val recipesToBeDeleted = mutableStateListOf<String>("")
    private val _toRecipeListScreenEvents = Channel<ToRecipeListScreenEvents>()
    val toRecipeListScreenEvents = _toRecipeListScreenEvents.receiveAsFlow()
    private val _isRefreshingState = mutableStateOf(false)
    val isRefreshingState: State<Boolean> = _isRefreshingState

    init {
        category.value =
            savedStateHandle.get<String>(Constants.RECIPE_LIST_SCREEN_RECIPE_CATEGORY_KEY)!!.trim()
        imageUrl.value =
            savedStateHandle.get<String>(Constants.RECIPE_LIST_SCREEN_RECIPE_IMAGE_URL_KEY)!!
        val temp = URLDecoder.decode(imageUrl.value, StandardCharsets.UTF_8.toString())
        Log.d(
            "recipelistviewmodel",
            "category is ${category.value}, image url is ${imageUrl.value}, temp is $temp"
        )
        getSavedRecipes.value = savedStateHandle.get<Boolean>(Constants.RECIPE_SCREEN_SHOULD_LOAD_FROM_SAVED_RECIPES) ?: false
        Log.d(
            "recipelistviewmodel",
            "get saved recipes is ${getSavedRecipes.value}"
        )
    }


    init {
        viewModelScope.launch {
            loadNextItems()
        }
    }

    fun onSearchBoxValueChanged(newValue: String) {
        _searchBoxState.value = newValue
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            searchRecipe(_searchBoxState.value).reset()
            state = state.copy(page = 0, items = emptyList())
            searchRecipe(_searchBoxState.value).loadNextItems()
        }
    }

    suspend fun loadNextItems() {
        searchRecipe(searchBoxState.value).loadNextItems()
    }

    private fun searchRecipe(recipe: String): RecipePaginator<Int, RecipeDtoItem> {
        return RecipePaginator<Int, RecipeDtoItem>(
            initialKey = state.page,
            onLoadUpdated = {
                state = state.copy(isLoading = it)
            },
            onRequest = { nextPage ->
                recipeRepository.getRecipesByCategory(
                    category = category.value,
                    page = state.page,
                    pageSize = 20,
                    fetchFromRemote = false,
                    recipe = recipe,
                    getSavedRecipes = getSavedRecipes.value
                )
            },
            getNextKey = { items ->
                state.page + 1
            },
            onError = { throwable ->
                state = state.copy(
                    error = throwable?.localizedMessage
                        ?: "unable to load items, please try again later",
                    isLoading = false
                )
            }
        ) { newItems, newKey ->
            state = state.copy(
                items = state.items + newItems,
                page = newKey,
                endReached = newItems.isEmpty(),
            )
        }
    }

    fun onClearSearchBoxButtonClicked() {
        _searchBoxState.value = ""
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            state = state.copy(page = 0, items = emptyList())
            val paginator = searchRecipe("")
            paginator.reset()
            paginator.loadNextItems()
        }
    }

    fun onSelectRadioButtonClicked(title: String){
        if(recipesToBeDeleted.contains(title)){
            recipesToBeDeleted.remove(title)
        }else{
            recipesToBeDeleted.add(title)
        }
    }

    fun onEvent(event: ToRecipeListScreenEvents){
        viewModelScope.launch {
            when (event) {
                ToRecipeListScreenEvents.NavigateUp -> {
                    _toRecipeListScreenEvents.send(ToRecipeListScreenEvents.NavigateUp)
                }
                is ToRecipeListScreenEvents.ShowSnackbar -> {
                    _toRecipeListScreenEvents.send(ToRecipeListScreenEvents.ShowSnackbar(event.message))
                }
            }
        }
    }

    fun receiveFromRecipeListScreenEvents(event: FromRecipeListScreenEvents){
        viewModelScope.launch {
            when(event){
                FromRecipeListScreenEvents.DisableEditMode -> {
                    recipesToBeDeleted.clear()
                    isEditModeOn.value = false
                }
                FromRecipeListScreenEvents.DeleteButtonClicked -> {
                    val result = recipeRepository.deleteSelectedSavedRecipes(recipeTitles = recipesToBeDeleted)
                    onClearSearchBoxButtonClicked()
                    onEvent(ToRecipeListScreenEvents.ShowSnackbar(result))
                }
                FromRecipeListScreenEvents.ChangeRefreshState -> {
                    _isRefreshingState.value = !_isRefreshingState.value
                }
            }
        }
    }
}

sealed interface ToRecipeListScreenEvents{
    object NavigateUp: ToRecipeListScreenEvents
    class ShowSnackbar(val message: String): ToRecipeListScreenEvents
}

sealed interface FromRecipeListScreenEvents{
    object DisableEditMode: FromRecipeListScreenEvents
    object DeleteButtonClicked: FromRecipeListScreenEvents
    object ChangeRefreshState : FromRecipeListScreenEvents
}