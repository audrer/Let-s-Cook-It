package com.jatinvashisht.letscookit.ui.categories_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import com.jatinvashisht.letscookit.core.MyPadding
import com.jatinvashisht.letscookit.core.Screen
import com.jatinvashisht.letscookit.core.lemonMilkFonts
import com.jatinvashisht.letscookit.data.remote.dto.categories.CategoryDtoItem
import okhttp3.internal.immutableListOf
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun CategoriesScreen(
    navController: NavHostController,
    categoriesScreenViewModel: CategoriesScreenViewModel = hiltViewModel(),
) {
    val lazyGridState = rememberLazyGridState()
    val categoriesListState = categoriesScreenViewModel.categoryListState.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Categories", color = Color.White)
                },
                backgroundColor = Color(0xFF911A17),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                }
            )
        }
    ) {padding->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            when {
                categoriesListState.isLoading -> {
                    CircularProgressIndicator(color = MaterialTheme.colors.secondary,
                        modifier = Modifier.align(
                            Alignment.Center))
                }
                categoriesListState.error.isNotBlank() -> {
                    Text(text = categoriesListState.error,
                        style = MaterialTheme.typography.subtitle2,
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyVerticalGrid(columns = GridCells.Fixed(2),
                        state = lazyGridState,
                        contentPadding = PaddingValues(4.dp)) {
                        items(categoriesListState.categories) { category ->
                            CategoryItem(
                                categoryDtoItem = category,
                                modifier = Modifier
                                    .size(200.dp)
                                    .clickable {
                                        navController.navigate(
                                            route = Screen.RecipeListScreen.route + "/${category.category}/${
                                                URLEncoder.encode(
                                                    category.imageUrl,
                                                    StandardCharsets.UTF_8.toString()
                                                )
                                            }/false"
                                        ) { launchSingleTop = true }
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    modifier: Modifier = Modifier,
    categoryDtoItem: CategoryDtoItem,
) {
    Box(modifier = modifier
        .padding(8.dp)
        .graphicsLayer {
            shape = RoundedCornerShape(MyPadding.medium)
            clip = true
        }
    ) {
        SubcomposeAsyncImage(
            model = categoryDtoItem.imageUrl,
            contentDescription = "category",
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .align(Alignment.BottomCenter)
            .drawBehind {
                drawRect(Brush.verticalGradient(colors = immutableListOf(Color.Transparent,
                    Color.Black)), alpha = 0.7f)
            }
        )
        Column(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = categoryDtoItem.category,
                fontFamily = lemonMilkFonts,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MyPadding.small),
                textAlign = TextAlign.Center
            )
        }
    }
}