package com.jatinvashisht.letscookit.domain.repository

import com.jatinvashisht.letscookit.core.Resource
import com.jatinvashisht.letscookit.data.local.LocalRecipeEntity
import com.jatinvashisht.letscookit.data.local.RecipeEntity
import com.jatinvashisht.letscookit.data.remote.dto.categories.CategoryDtoItem
import com.jatinvashisht.letscookit.data.remote.dto.recipes.RecipeDtoItem
import com.jatinvashisht.letscookit.domain.model.ModelLocalRecipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun getRecipes(
        recipe: String,
        page: Int,
        pageSize: Int,
        fetchFromRemote: Boolean,
        ): Resource<List<RecipeDtoItem>>

    suspend fun getFirstFourRecipes(fetchFromRemote: Boolean): Resource<List<RecipeDtoItem>>

    suspend fun getRecipeByTitle(title: String, category: String): Flow<Resource<RecipeDtoItem>>

    suspend fun getCategories(): Flow<Resource<List<CategoryDtoItem>>>

    suspend fun getRecipesByCategory(
        recipe: String,
        category: String,
        page: Int,
        pageSize: Int,
        fetchFromRemote: Boolean,
        getSavedRecipes: Boolean,
        ): Resource<List<RecipeDtoItem>>

    suspend fun getSavedRecipes(): Resource<List<ModelLocalRecipe>>

    suspend fun saveRecipe(recipeDtoItem: RecipeDtoItem): Resource<String>

    suspend fun getLocalRecipeByTitle(title: String): Resource<ModelLocalRecipe?>

    suspend fun deleteSelectedSavedRecipes(recipeTitles: List<String>): String

    suspend fun getGyroscopeData(): Resource<GyroscopeData>
    data class GyroscopeData(
        val x: Float,
        val y: Float,
        val z: Float
    )
}