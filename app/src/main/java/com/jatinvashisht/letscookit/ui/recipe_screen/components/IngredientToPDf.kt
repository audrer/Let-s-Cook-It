package com.jatinvashisht.letscookit.ui.recipe_screen.components

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.text.*
import android.util.Log
import androidx.core.graphics.withTranslation
import com.jatinvashisht.letscookit.data.remote.dto.recipes.Ingredient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//@Composable
fun ingredientToPDF(
//    modifier: Modifier = Modifier,
    ingredient: List<Ingredient>,
    name: String
//    onButtonClick: (PdfDocument) -> Unit
): PdfDocument {
    val pdfDocument = PdfDocument()
    val pageWidth = 720
    val pageHeight = 1190
    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1)
        .create()


    val page = pdfDocument.startPage(pageInfo)
    Log.d("ingredienttopdf", "ingredient size is ${ingredient.size}")
    if (ingredient.size < 22) {
        page.canvas.apply {
            getCanvasContent(canvas = this, ingredient = ingredient, name = name)
        }
        pdfDocument.finishPage(page)
    }else {
        page.canvas.apply {
            getCanvasContent(canvas = this, ingredient = ingredient.subList(0, 22), name = name)
        }
        pdfDocument
            .finishPage(page)
        val page2 =
            pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create())
        page2.canvas.apply {
            getCanvasContent(canvas = this, ingredient = ingredient.subList(22, ingredient.lastIndex), name = name)
        }
        pdfDocument.finishPage(page2)
    }

//    var i = 0
//    var pageno = 1
//    val size = ingredient.size
//    CoroutineScope(IO).launch {
//        while (i < size) {
//            if (i + 22 > size) {
//                val page2 =
//                    pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth,
//                        pageHeight,
//                        pageno).create())
//                page2.canvas.apply {
//                    getCanvasContent(canvas = this,
//                        ingredient = ingredient.subList(i, ingredient.lastIndex))
//                }
//                pdfDocument.finishPage(page2)
//                break
//            }
//
//            val page2 =
//                pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageno)
//                    .create())
//            page2.canvas.apply {
//                getCanvasContent(canvas = this, ingredient = ingredient.subList(i, i + 22))
//            }
//            pdfDocument.finishPage(page2)
//            delay(1000)
//            i += 22
//            pageno++
//        }
//    }

    return pdfDocument
}

fun getCanvasContent(canvas: Canvas, ingredient: List<Ingredient>, name: String): Canvas {
    return canvas.apply {
        val width = this.width
        drawRect(Rect(0, 0, this.width, this.height), Paint().apply {
            color = Color.YELLOW
        })

        drawText("Let's Cook", 10f, 52f, Paint().apply {
            this.textSize = 40f
            this.color = Color.BLACK
        })

        drawText(name, 210f, 52f, Paint().apply {
            this.textSize = 40f
            this.color = Color.BLACK
        })

        drawLine(0f, 60f, width.toFloat(), 60f, Paint().apply {
            strokeWidth = 5f
            this.color = Color.BLACK
        })
        val x = 5f
        var y = 65f
        for (i in ingredient) {
            drawMultilineText(
                text = i.toString(),
                textPaint = TextPaint().apply {
                    this.color = Color.BLACK
                    this.textSize = 30f
                },
                width = width - 10,
                x = x,
                y = y,
                start = 0
            )
            y += 50
            if (y > this.height) {

            }
        }
    }
}

fun Canvas.drawMultilineText(
    text: CharSequence,
    textPaint: TextPaint,
    width: Int,
    x: Float,
    y: Float,
    start: Int = 0,
    end: Int = text.length,
    alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
    textDir: TextDirectionHeuristic = TextDirectionHeuristics.LTR,
    spacingMult: Float = 1f,
    spacingAdd: Float = 0f,
    hyphenationFrequency: Int = Layout.HYPHENATION_FREQUENCY_NONE,
    justificationMode: Int = Layout.JUSTIFICATION_MODE_NONE,
) {

    val staticLayout = StaticLayout.Builder
        .obtain(text, start, end, textPaint, width)
        .setAlignment(alignment)
        .setTextDirection(textDir)
        .setLineSpacing(spacingAdd, spacingMult)
        .build()


    withTranslation(x, y) {
        staticLayout.draw(this)
    }
}