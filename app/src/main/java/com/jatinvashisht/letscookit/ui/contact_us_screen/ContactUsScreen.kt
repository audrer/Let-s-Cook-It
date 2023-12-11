package com.jatinvashisht.letscookit.ui.contact_us_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText

@Composable
fun ContactUsScreen(navController: NavHostController) {
    RichText(modifier = Modifier.fillMaxSize()) {
        Markdown(content = """
            # Obligate Solutions
            [click here](http://obligatesolutions.in/) to visit our website
            ## Address
            F-49, Vatsalya Status, Near Railway Station, Kadi, Gujarat
            ## Contact Us
            [contact@obligatesolutions.in](contact@obligatesolutions.in)
            you can contact us on our official email address if you have any queries or suggestions regarding our product
            ```
            contact@obligatesolutions.in
            ```
        """.trimIndent())
    }
}