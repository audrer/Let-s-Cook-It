package com.jatinvashisht.letscookit.ui.custom_view

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class CustomShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height/2)
            arcToRad(
                rect = Rect(center = Offset(size.width/2, -150f), radius = size.width + 110f),
                startAngleRadians = 0f,
                sweepAngleRadians = 180f,
                false
            )
            close()
        }
        return Outline.Generic(path)
    }

}