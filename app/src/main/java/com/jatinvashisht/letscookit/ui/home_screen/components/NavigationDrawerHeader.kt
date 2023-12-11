package com.jatinvashisht.letscookit.ui.home_screen.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@Composable
fun NavigationDrawerHeader(
    modifier: Modifier,
    context: Context,
) {
        SubcomposeAsyncImage(
            model = "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=481&q=80",
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentScale = ContentScale.Crop
        )
}