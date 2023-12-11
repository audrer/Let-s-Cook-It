package com.jatinvashisht.letscookit.ui.contact_us_screen

import android.content.Context
import android.content.Intent
import android.net.Uri


fun openEmail(email: String, context: Context) {
    val emailIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(email))
        type = "*/*"
    }

//    emailIntent.type = "message/rfc822"

    context.startActivity(emailIntent)
}