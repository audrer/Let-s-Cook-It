package com.jatinvashisht.letscookit.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface RecipeDao{
    @Insert(entity = RecipeEntity::class,onConflict = REPLACE)
    suspend fun insertRecipes(recipeEntities: List<RecipeEntity>)

    @Query("DELETE FROM recipeentity")
    suspend fun clearRecipes()

    @Query("""
        SELECT *
        FROM recipeentity
        WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%'
    """)
    suspend fun searchRecipe(query: String): List<RecipeEntity>

    @Query("SELECT * FROM recipeentity WHERE title LIKE '%pizza%' OR title LIKE '%burger%' LIMIT 10")
    suspend fun getFirstFourRecipes(): List<RecipeEntity>

    @Query("SELECT * FROM recipeentity WHERE title = :recipeTitle")
    suspend fun getRecipeByTitle(recipeTitle: String): RecipeEntity?

    @Query("SELECT * FROM recipeentity WHERE tag LIKE '%' || :category ||  '%'  AND LOWER(title) LIKE '%' || LOWER(:recipe) || '%' ")
    suspend fun getRecipeByTag(category: String, recipe: String): List<RecipeEntity>

    @Insert(entity = LocalRecipeEntity::class, onConflict = REPLACE)
    suspend fun saveRecipe(localRecipeEntity: LocalRecipeEntity)

    @Query("SELECT * FROM localrecipeentity")
    suspend fun getSavedRecipes(): List<LocalRecipeEntity>

    @Query("DELETE FROM localrecipeentity WHERE title = :title")
    suspend fun deleteLocalRecipeEntity(title: String)

    @Query("SELECT * FROM localrecipeentity WHERE title = :title")
    suspend fun getSavedRecipeByTitle(title: String): LocalRecipeEntity?

    @Query("DELETE FROM localrecipeentity")
    suspend fun deleteAllSavedRecipes()

    @Query("SELECT * FROM localrecipeentity WHERE LOWER(title) LIKE '%' || LOWER(:recipe) || '%' ")
    suspend fun searchSavedRecipe(recipe: String): List<LocalRecipeEntity>

    @Query("DELETE FROM localrecipeentity WHERE title IN (:titles)")
    suspend fun deleteLocalRecipes(titles: List<String>)

    @Query("SELECT * FROM localrecipecategoryentity")
    suspend fun getAllCategories(): List<LocalRecipeCategoryEntity>

    @Insert(entity = LocalRecipeCategoryEntity::class, onConflict = REPLACE)
    suspend fun insertLocalCategories(categories: List<LocalRecipeCategoryEntity>)

    @Query("DELETE FROM localrecipecategoryentity")
    suspend fun deleteAllCategories()
}