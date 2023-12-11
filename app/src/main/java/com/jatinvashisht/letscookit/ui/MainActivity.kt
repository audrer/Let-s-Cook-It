package com.jatinvashisht.letscookit.ui

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.jatinvashisht.letscookit.core.Constants
import com.jatinvashisht.letscookit.core.Screen
import com.jatinvashisht.letscookit.ui.categories_screen.CategoriesScreen
import com.jatinvashisht.letscookit.ui.contact_us_screen.ContactUsComposable
import com.jatinvashisht.letscookit.ui.contact_us_screen.ContactUsScreen
import com.jatinvashisht.letscookit.ui.home_screen.HomeScreen
import com.jatinvashisht.letscookit.ui.recipe_list_screen.RecipeListScreen
import com.jatinvashisht.letscookit.ui.recipe_screen.RecipeScreen
import com.jatinvashisht.letscookit.ui.theme.LetsCookItTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val directory = getDirectory()
        val file = File(directory, "ingredientlist.pdf")
        setContent {
            LetsCookItTheme {
                val permissionState =
                    rememberMultiplePermissionsState(permissions = listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE))
                val allPermissionsGranted by remember{ mutableStateOf(permissionState.allPermissionsGranted)}
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                  val navController = rememberNavController()
                  NavHost(
                      navController = navController,
                      startDestination = Screen.HomeScreen.route
                  ) {
                      composable(route = Screen.HomeScreen.route) {
                          HomeScreen(navController = navController){
                              finish()
                          }
                      }
                      composable(route = Screen.RecipeScreen.route + "/{${Constants.RECIPE_SCREEN_RECIPE_TITLE_KEY}}/{${Constants.RECIPE_SCREEN_RECIPE_CATEGORY_KEY}}/{${Constants.RECIPE_SCREEN_SHOULD_LOAD_FROM_SAVED_RECIPES}}",
                          arguments = listOf(
                              navArgument(name = Constants.RECIPE_SCREEN_RECIPE_TITLE_KEY) {
                                  type = NavType.StringType
                              },
                              navArgument(name = Constants.RECIPE_SCREEN_RECIPE_CATEGORY_KEY) {
                                  type = NavType.StringType
                              },
                              navArgument(name = Constants.RECIPE_SCREEN_SHOULD_LOAD_FROM_SAVED_RECIPES)
                              {
                                  type = NavType.BoolType
                              },
                          )) {
                          RecipeScreen(navController = navController)

                      }
                      composable(
                          route = Screen.RecipeListScreen.route + "/{${Constants.RECIPE_LIST_SCREEN_RECIPE_CATEGORY_KEY}}/{${Constants.RECIPE_LIST_SCREEN_RECIPE_IMAGE_URL_KEY}}/{${Constants.RECIPE_SCREEN_SHOULD_LOAD_FROM_SAVED_RECIPES}}",
                          arguments = listOf(
                              navArgument(name = Constants.RECIPE_LIST_SCREEN_RECIPE_CATEGORY_KEY) {
                                  type = NavType.StringType
                              },
                              navArgument(name = Constants.RECIPE_LIST_SCREEN_RECIPE_IMAGE_URL_KEY)
                              {
                                  type = NavType.StringType
                              },
                              navArgument(name = Constants.RECIPE_SCREEN_SHOULD_LOAD_FROM_SAVED_RECIPES)
                              {
                                  type = NavType.BoolType
                              },
                          )
                      ) {
                          RecipeListScreen(navController = navController)
                      }

                      composable(route = Screen.CategoriesScreen.route){
                          CategoriesScreen(
                              navController = navController
                          )
                      }

                      composable(route = Screen.ContactUsScreen.route){
                          ContactUsComposable(navController = navController, context = this@MainActivity)
                      }
                  }
              }
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun CheckForPermissions() {


    }
    private fun getDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let{
            File(it, resources.getString(com.jatinvashisht.letscookit.R.string.app_name))
                .apply {
                    mkdir()
                }
        }
        return if(mediaDir != null && mediaDir.exists()) mediaDir else filesDir
}
    
}

