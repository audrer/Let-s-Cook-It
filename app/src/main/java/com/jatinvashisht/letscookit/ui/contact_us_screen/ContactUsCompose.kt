package com.jatinvashisht.letscookit.ui.contact_us_screen

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.navigation.NavHostController
import com.jatinvashisht.letscookit.core.MyPadding
import com.jatinvashisht.letscookit.ui.home_screen.components.openWebPage

@Composable
fun ContactUsComposable(
    navController: NavHostController,
    context: Context
) {
    Box(modifier = Modifier.fillMaxSize()){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(MyPadding.small)
        ) {
            Text(text = "Obligate Solutions", style = MaterialTheme.typography.h4,)
            Text(
                text = "Click here to visit our website",
                color = Color.Yellow,
                modifier = Modifier.clickable {
                    openWebPage(url = "https://obligatesolutions.in/", context = context)
                }
            )
            Spacer(modifier = Modifier.height(MyPadding.medium))
            Text(text = "Obligate solutions is a software solutions company which aims to provide its customer with best service possible")
            Spacer(modifier = Modifier.height(MyPadding.medium))
            Text(text = "We have managed to server more than 50 customers as of now and numbers are going up")
            Spacer(modifier = Modifier.height(MyPadding.medium))
            Text(text = "If you have any queries you can contact us at our official mail id")
            Spacer(modifier = Modifier.height(MyPadding.medium))
            Text(
                text = "contact@obligatesolutions.in",
                color = Color.Yellow,
                modifier = Modifier.clickable{
                    openEmail(email = "contact@obligatesolutions.in", context = context)
                }
            )
        }
    }
}