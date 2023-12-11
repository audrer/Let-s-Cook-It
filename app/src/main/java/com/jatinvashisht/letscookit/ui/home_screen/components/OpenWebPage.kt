package com.jatinvashisht.letscookit.ui.home_screen.components

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity

fun openWebPage(url: String, context: Context) {
    val webpage: Uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, webpage)
//    if (intent.resolveActivity(packageManager) != null) {
        context.startActivity(intent)
//    }
}