package com.jatinvashisht.letscookit.ui.recipe_screen

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.jatinvashisht.letscookit.R
import com.jatinvashisht.letscookit.core.MyPadding
import com.jatinvashisht.letscookit.core.lemonMilkFonts
import com.jatinvashisht.letscookit.data.remote.dto.recipes.Ingredient
import com.jatinvashisht.letscookit.ui.custom_view.CustomShape
import com.jatinvashisht.letscookit.ui.recipe_screen.components.ingredientToPDF
import kotlinx.coroutines.flow.collectLatest
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecipeScreen(
    navController: NavHostController,
    viewModel: RecipeScreenViewModel = hiltViewModel(),
) {
    val screenState = viewModel.recipeState.value
    val scaffoldState = rememberScaffoldState()
    val numberOfPersons = viewModel.numberOfPersons.value
    val ingredients = screenState.recipe.ingredient
    val favoriteButtonState = viewModel.favouriteState.value
    val multiplePermissionState =
        rememberMultiplePermissionsState(permissions = listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.uiRecipeScreenEvents.collectLatest { event ->
            when (event) {
                is RecipeScreenEvents.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }

    when {
        screenState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colors.primaryVariant)
            }
        }
        screenState.error.isNotBlank() -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = screenState.error, color = MaterialTheme.colors.secondary)
            }
        }
        else -> {
            Scaffold(scaffoldState = scaffoldState) {padding->
                LazyColumn(modifier = Modifier.fillMaxSize()) {

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(LocalConfiguration.current.screenHeightDp.dp / 2)
                                .graphicsLayer {
                                    shadowElevation = 8.dp.toPx()
                                    shape = CustomShape()
                                    clip = true
                                }
                                .drawBehind {
                                    drawRect(color = Color(0xFF000000))
                                },
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.TopCenter)
                                    .drawBehind { drawRect(color = Color.Transparent) },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = { navController.navigateUp() },
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "goto home screen",
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {

                                    IconButton(onClick = {
                                        if (multiplePermissionState.shouldShowRationale) {
                                            viewModel.sendRecipeScreenUiEvent(RecipeScreenEvents.ShowSnackbar(
                                                "Please provide storage permission to continue"))
                                        } else if (!multiplePermissionState.allPermissionsGranted) {
                                            multiplePermissionState.launchMultiplePermissionRequest()
                                        } else {
                                            exportPdf(context = context, ingredients = ingredients, viewModel = viewModel, name = screenState.recipe.title)
                                        }
                                    }) {
                                        Icon(imageVector = Icons.Default.DownloadForOffline,
                                            contentDescription = "download recipe")
                                    }

                                    IconButton(
                                        onClick = viewModel::onSaveRecipeButtonClicked,
                                    ) {
                                        Icon(
                                            imageVector =
                                            when (favoriteButtonState) {
                                                RecipeSaveState.NOT_SAVED -> {
                                                    Icons.Outlined.Favorite
                                                }
                                                RecipeSaveState.ALREADY_EXISTS -> {
                                                    Icons.Default.Favorite
                                                }
                                                RecipeSaveState.SAVED -> {
                                                    Icons.Default.Favorite
                                                }
                                                else -> {
                                                    Icons.Outlined.Favorite
                                                }
                                            },
                                            contentDescription = "Save Recipe",
                                            tint = if (favoriteButtonState == RecipeSaveState.NOT_SAVED) Color.White else Color.Red
                                        )
                                    }
                                }
                            }

                            SubcomposeAsyncImage(
                                model = screenState.recipe.imageUrl,
                                loading = {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(50.dp),
                                        color = MaterialTheme.colors.primaryVariant
                                    )
                                },
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        this.alpha = 0.25f
                                        shadowElevation = 8.dp.toPx()
                                        clip = true
                                    }
                                    .align(Alignment.Center),
                                contentScale = ContentScale.Crop,
                                filterQuality = FilterQuality.Medium
                            )
                            Text(
                                text = screenState.recipe.title,
                                style = MaterialTheme.typography.h4,
                                fontWeight = FontWeight.ExtraLight,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colors.onSurface,
                                fontFamily = lemonMilkFonts,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
//                        Spacer(modifier = Modifier.height(MyPadding.medium))
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                            Text(
                                text = "Download Ingredients",
                                fontFamily = lemonMilkFonts,
                                fontWeight = FontWeight.Medium,
                                style = MaterialTheme.typography.h6,
                                modifier = Modifier.padding(horizontal = MyPadding.medium)
                                    .clickable{
                                        if (multiplePermissionState.shouldShowRationale) {
                                            viewModel.sendRecipeScreenUiEvent(RecipeScreenEvents.ShowSnackbar(
                                                "Please provide storage permission to continue"))
                                        } else if (!multiplePermissionState.allPermissionsGranted) {
                                            multiplePermissionState.launchMultiplePermissionRequest()
                                        } else {
                                            exportPdf(context = context,
                                                ingredients = ingredients,
                                                viewModel = viewModel,
                                                name = screenState.recipe.title)
                                        }

                                    }
                            )
                            Spacer(modifier = Modifier.height(MyPadding.medium))
                            IconButton(onClick = {
                                if (multiplePermissionState.shouldShowRationale) {
                                    viewModel.sendRecipeScreenUiEvent(RecipeScreenEvents.ShowSnackbar(
                                        "Please provide storage permission to continue"))
                                } else if (!multiplePermissionState.allPermissionsGranted) {
                                    multiplePermissionState.launchMultiplePermissionRequest()
                                } else {
                                    exportPdf(context = context,
                                        ingredients = ingredients,
                                        viewModel = viewModel,
                                        name = screenState.recipe.title)
                                }
                            }) {
                                Icon(imageVector = Icons.Default.DownloadForOffline,
                                    contentDescription = "download recipe")
                            }
                        }
                        Spacer(modifier = Modifier.height(MyPadding.medium))
                    }

                    item {
                        NumberOfPersonSlider(
                            currentValue = numberOfPersons,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = MyPadding.medium)
                        ) {
                            viewModel.onSliderValueChanged(it)
                            Log.d("recipescreen", "number of persons $numberOfPersons")
                        }
                    }

                    item {
                        Text(
                            text = "Ingredients",
                            fontFamily = lemonMilkFonts,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.h4,
                            modifier = Modifier.padding(horizontal = MyPadding.medium)
                        )
                        Spacer(modifier = Modifier.height(MyPadding.medium))
                    }


                    items(ingredients) { ingredient ->
                        val ingredientQuantity = ingredient.quantity.toFloatOrNull()
                            ?.times(viewModel.numberOfPersons.value)
                        val modifiedIngredient = if (ingredientQuantity == null) {
                            ""
                        } else {
                            "$ingredientQuantity "
                        }
                        Text(
                            text = " ${modifiedIngredient}${ingredient.description}",
                            fontFamily = lemonMilkFonts,
                            fontWeight = FontWeight.Normal,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(horizontal = MyPadding.medium)
                        )
                        Spacer(modifier = Modifier.height(MyPadding.medium))
                    }

                    item {
                        Text(
                            text = "Method",
                            fontFamily = lemonMilkFonts,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.h4,
                            modifier = Modifier.padding(horizontal = MyPadding.medium)
                        )
                        Spacer(modifier = Modifier.height(MyPadding.medium))
                    }

                    items(screenState.recipe.method) { method ->
                        Text(
                            text = method,
                            fontFamily = lemonMilkFonts,
                            fontWeight = FontWeight.Normal,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(horizontal = MyPadding.medium)
                        )
                        Spacer(modifier = Modifier.height(MyPadding.medium))
                    }
                }
            }
        }
    }
}

fun exportPdf(context: Context, ingredients: List<Ingredient>, viewModel: RecipeScreenViewModel, name: String) {

    val pdfDocument =
        ingredientToPDF(ingredient = ingredients, name = name)

    if (Build.VERSION.SDK_INT > 29) {
        val resolver = context.contentResolver
        val values = ContentValues()
        // save to a folder
        values.put(MediaStore.MediaColumns.DISPLAY_NAME,
            "${viewModel.recipeState.value.recipe.title}.pdf")
        values.put(MediaStore.MediaColumns.MIME_TYPE,
            "application/pdf")
        values.put(MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_DOWNLOADS + "/" + "letscook")
        val uri =
            resolver.insert(MediaStore.Files.getContentUri("external"),
                values)
        // You can use this outputStream to write whatever file you want:
        val outputStream = resolver.openOutputStream(uri!!)

        try {
            pdfDocument.writeTo(outputStream)
            Toast.makeText(context,
                "file saved successfully at Downloads/letscook/${viewModel.recipeState.value.recipe.title}.pdf",
                Toast.LENGTH_LONG).show()
            Log.d("mainactivity",
                "file saved successfully at ${uri}")

        } catch (e: Exception) {
            Log.d("mainactivity",
                "error occurred while saving file\n $e")
            Toast.makeText(context,
                "unable to save file",
                Toast.LENGTH_LONG).show()
        }

    } else {
        val file =
            File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS),
                "letscook/ingredients.pdf"
            )

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(context,
                "file saved successfully at Downloads/letscook/ingredient.pdf",
                Toast.LENGTH_LONG).show()
            Log.d("mainactivity",
                "file saved successfully at ${file.absolutePath}/${file.name}")

        } catch (e: Exception) {
            Log.d("mainactivity",
                "error occurred while saving file\n $e")
            Toast.makeText(context,
                "unable to save file",
                Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun NumberOfPersonSlider(
    modifier: Modifier = Modifier,
    currentValue: Int,
    onValueChanged: (Float) -> Unit,
) {
    Log.d("recipescreen", "current value is $currentValue")
    Column(modifier = modifier) {
        Text(
            text = "Number of persons",
            fontWeight = FontWeight.Medium,
            fontFamily = lemonMilkFonts,
            style = MaterialTheme.typography.h5)
        Row(
//            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Slider(
                value = currentValue.toFloat() / 10f,
                onValueChange = onValueChanged,
                modifier = Modifier.fillMaxWidth(0.75f),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colors.primaryVariant,
                    activeTrackColor = MaterialTheme.colors.primaryVariant,
                )
            )
            Spacer(modifier = Modifier.width(MyPadding.small))
            Text(text = "$currentValue", modifier = Modifier.fillMaxWidth())
        }
    }
}

private fun getDirectory(context: Context): File {
    val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
        File(it, context.resources.getString(R.string.app_name))
            .apply {
                mkdir()
            }
//        val mediaDir = context.getExternalFilesDir(null).apply {
//            File(this?.name ?: "letscook").apply {
//                mkdir()
//            }
    }


//    val dir = context.externalCacheDirs
    return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
}

