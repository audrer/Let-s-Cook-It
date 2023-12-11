package com.jatinvashisht.letscookit.ui.recipe_screen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jatinvashisht.letscookit.core.Constants
import com.jatinvashisht.letscookit.core.Resource
import com.jatinvashisht.letscookit.data.mapper.toRecipeDtoItem
import com.jatinvashisht.letscookit.data.remote.dto.recipes.RecipeDtoItem
import com.jatinvashisht.letscookit.domain.repository.RecipeRepository
import com.jatinvashisht.letscookit.domain.usecases.UseCaseGetRecipeSavedStatus
import com.jatinvashisht.letscookit.domain.usecases.UseCaseSaveRecipe
import com.jatinvashisht.letscookit.ui.recipe_screen.components.RecipeScreenState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlin.math.ceil

@HiltViewModel
class RecipeScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val recipeRepository: RecipeRepository,
    private val useCaseSaveRecipe: UseCaseSaveRecipe,
    private val useCaseGetRecipeSavedStatus: UseCaseGetRecipeSavedStatus
) : ViewModel() {
    private val _recipeState = mutableStateOf<RecipeScreenState>(RecipeScreenState())
    val recipeState: State<RecipeScreenState> = _recipeState

    private val recipeTitle = mutableStateOf("")
    private val recipeCategory = mutableStateOf("")

    private val _uiRecipeScreenEvents: Channel<RecipeScreenEvents> = Channel()
    val uiRecipeScreenEvents = _uiRecipeScreenEvents.receiveAsFlow()

    private val numberOfPersonsState = mutableStateOf<Int>(1)
    val numberOfPersons = numberOfPersonsState as State<Int>

    private val _favouriteState = mutableStateOf(RecipeSaveState.UNABLE_TO_SAVE)
    val favouriteState: State<RecipeSaveState> = _favouriteState

    init {
        val recipe = savedStateHandle.get<String>(Constants.RECIPE_SCREEN_RECIPE_TITLE_KEY)
        val decodedTitle = URLDecoder.decode(recipe, StandardCharsets.UTF_8.toString())
        val category = savedStateHandle.get<String>(Constants.RECIPE_SCREEN_RECIPE_CATEGORY_KEY)
        val decodedCategory = URLDecoder.decode(category, StandardCharsets.UTF_8.toString())
        viewModelScope.launch {
            recipeTitle.value = decodedTitle ?: ""

            recipeCategory.value = decodedCategory ?: ""
            Log.d("recipescreenviewmodel", "recipe is $recipe")

            val shouldLoadFromSavedRecipes = savedStateHandle.get<Boolean>(Constants.RECIPE_SCREEN_SHOULD_LOAD_FROM_SAVED_RECIPES) ?: true
            Log.d("recipescreenviewmodel", "should load from saved recipes is $shouldLoadFromSavedRecipes")
            getRecipe(shouldLoadFromSavedRecipes = shouldLoadFromSavedRecipes)
            setFavouriteState()
        }
    }

    private suspend fun getRecipe(shouldLoadFromSavedRecipes: Boolean) {
        if (shouldLoadFromSavedRecipes) {
            val recipeResult = recipeRepository.getLocalRecipeByTitle(title = recipeTitle.value)
            when (recipeResult) {
                is Resource.Error -> {
                    _recipeState.value = _recipeState.value.copy(
                        isLoading = false,
                        error = "Unable to load recipe. Please try again later"
                    )
                }
                is Resource.Loading -> {
                    _recipeState.value = _recipeState.value.copy(isLoading = true, error = "")
                }
                is Resource.Success -> {
                    _recipeState.value = _recipeState.value.copy(
                        isLoading = false,
                        recipe = recipeResult.data?.toRecipeDtoItem() ?: RecipeDtoItem()
                    )
                }
            }
        } else {
            recipeRepository.getRecipeByTitle(
                title = recipeTitle.value,
                category = recipeCategory.value
            ).collectLatest { recipeResult ->
                when (recipeResult) {
                    is Resource.Error -> {
                        _recipeState.value = _recipeState.value.copy(
                            isLoading = false,
                            error = "Unable to load recipe. Please try again later"
                        )
                    }
                    is Resource.Loading -> {
                        _recipeState.value = _recipeState.value.copy(isLoading = true, error = "")
                    }
                    is Resource.Success -> {
                        _recipeState.value = _recipeState.value.copy(
                            isLoading = false,
                            recipe = recipeResult.data ?: RecipeDtoItem()
                        )
                    }
                }
            }
        }
    }

    fun sendRecipeScreenUiEvent(uiEvents: RecipeScreenEvents) {
        viewModelScope.launch {
            when (uiEvents) {
                is RecipeScreenEvents.ShowSnackbar -> _uiRecipeScreenEvents.send(
                    RecipeScreenEvents.ShowSnackbar(
                        message = uiEvents.message
                    )
                )
            }
        }
    }

    private fun setFavouriteState(){
        viewModelScope.launch {
            val currentRecipe = _recipeState.value.recipe
            useCaseGetRecipeSavedStatus(title = currentRecipe.title).collectLatest {
                _favouriteState.value = it
            }
            Log.d("recipescreenviewmodel","favourite state is ${recipeTitle.value}")
        }
    }

    fun onSaveRecipeButtonClicked() {
        viewModelScope.launch {
            val currentRecipe = _recipeState.value.recipe
            useCaseSaveRecipe(recipeDtoItem = currentRecipe).collectLatest {
                _favouriteState.value = it
            }
            Log.d("recipescreenviewmodel","favourite state is ${_favouriteState.value.name}")
            when (_favouriteState.value){
                RecipeSaveState.SAVED -> {
                    sendRecipeScreenUiEvent(RecipeScreenEvents.ShowSnackbar("Recipe SAVED successfully"))
                }
                RecipeSaveState.ALREADY_EXISTS -> {
                    sendRecipeScreenUiEvent(RecipeScreenEvents.ShowSnackbar("Recipe ALREADY exists"))
                }
                RecipeSaveState.UNABLE_TO_SAVE -> {
                    sendRecipeScreenUiEvent(RecipeScreenEvents.ShowSnackbar("UNABLE to save recipe, try again"))
                }
                RecipeSaveState.NOT_SAVED -> {
                    sendRecipeScreenUiEvent(RecipeScreenEvents.ShowSnackbar("REMOVED from favourites"))
                }
            }

        }
    }

    fun onSliderValueChanged(newValue: Float){
        Log.d("recipescreenviewmodel", "new value of slider is $newValue value of ceil is ${ceil(newValue*10)}")
        numberOfPersonsState.value = if(newValue <= 0.1f) 1 else (newValue*10).toInt()
    }

}

sealed interface RecipeScreenEvents {
    class ShowSnackbar(val message: String) : RecipeScreenEvents
}