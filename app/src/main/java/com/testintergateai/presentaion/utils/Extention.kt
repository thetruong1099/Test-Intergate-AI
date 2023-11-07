package com.testintergateai.presentaion.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import com.google.mlkit.vision.face.Face
import kotlin.math.abs

fun Bitmap.rotateBitmap(degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, false)
}

fun List<Face>.toListBitmap(bitmap: Bitmap, width: Int, height: Int) =
    map { bitmap.cropRectFromBitmap(it.boundingBox).resized(width, height) }

fun Bitmap.cropRectFromBitmap(rect: Rect): Bitmap {
    var width = rect.width()
    var height = rect.height()
    rect.left = if (rect.left > 0) rect.left else 0
    rect.top = if (rect.top > 0) rect.top else 0
    if ((rect.left + width) > this.width) {
        width = abs(this.width - rect.left)
    }
    if ((rect.top + height) > this.height) {
        height = abs(this.height - rect.top)
    }

    return Bitmap.createBitmap(this, rect.left, rect.top, width, height)
}

fun Bitmap.resized(width: Int, height: Int): Bitmap {
    return Bitmap.createScaledBitmap(
        this,
        width,
        height,
        true
    )
}