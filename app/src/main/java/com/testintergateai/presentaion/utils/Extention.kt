package com.testintergateai.presentaion.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import androidx.compose.ui.graphics.asImageBitmap
import com.google.mlkit.vision.face.Face
import kotlin.math.abs

fun Bitmap.centerCrop(desiredWidth: Int, desiredHeight: Int): Bitmap {
    val xStart = (width - desiredWidth) / 2
    val yStart = (height - desiredHeight) / 2

    if (xStart < 0 || yStart < 0 || desiredWidth > width || desiredHeight > height) {
        throw IllegalArgumentException("Invalid arguments for center cropping")
    }

    return Bitmap.createBitmap(this, xStart, yStart, desiredWidth, desiredHeight)
}

fun convertFaceObjectToBitmap(face: Face, bitmap: Bitmap): Bitmap {
    // Get the face object's bounding box.
    val boundingBox = face.boundingBox

    // Crop the image to the bounding box.
    val croppedBitmap = Bitmap.createBitmap(
        bitmap,
        boundingBox.left,
        boundingBox.top,
        boundingBox.width(),
        boundingBox.height()
    )

    // Resize the cropped image to the desired size for input to TensorFlow.
    val resizedBitmap = Bitmap.createScaledBitmap(
        croppedBitmap,
        321,
        321,
        true
    )

    // Convert the resized image to a bitmap.
    return resizedBitmap
}

fun getFaceImage(face: Face, bitmap: Bitmap): Bitmap {
    val bounds = face.boundingBox
    val theStartPoint = if (bounds.left < 0) 0 else bounds.left
    val theEndPoint = if (bounds.right > bitmap.width) bitmap.width else bounds.right
    val theTopPoint = if (bounds.top < 0) 0 else bounds.top
    val theBottomPoint = if (bounds.bottom > bitmap.height) bitmap.height else bounds.bottom

    val faceWith = theEndPoint - theStartPoint
    val faceHeight = theBottomPoint - theTopPoint

    val startPointPercent = theStartPoint.toFloat() / bitmap.width.toFloat()
    val topPointPercent = theTopPoint.toFloat() / bitmap.height.toFloat()

    val faceWidthPercent = faceWith / bitmap.width.toFloat()
    val faceHeightPercent = faceHeight / bitmap.height.toFloat()

    val croppedBitmap = Bitmap.createBitmap(
        bitmap,
        theStartPoint,
        theTopPoint,
        faceWith,
        faceHeight
    )

    return Bitmap.createScaledBitmap(
        croppedBitmap,
        321,
        321,
        true
    )
}

fun Face.getFaceRect(bitmap: Bitmap): Rect {
    val bounds = this.boundingBox
    val theStartPoint = if (bounds.left < 0) 0 else bounds.left
    val theEndPoint = if (bounds.right > bitmap.width) bitmap.width else bounds.right
    val theTopPoint = if (bounds.top < 0) 0 else bounds.top
    val theBottomPoint = if (bounds.bottom > bitmap.height) bitmap.height else bounds.bottom
    return Rect().apply {
        left = theStartPoint
        right = theEndPoint
        top = theTopPoint
        bottom = theBottomPoint
    }
}

fun List<Face>.toListRect(bitmap: Bitmap) = map { it.getFaceRect(bitmap) }

fun List<Face>.toListRect() = map { it.boundingBox }
fun List<Face>.toListBitmap(bitmap: Bitmap) =
    map { bitmap.cropRectFromBitmap(it.boundingBox).resizedForTfLite() }

fun List<Bitmap?>.toListImageBitmap() = map { it?.asImageBitmap() }

fun Bitmap.cropToBox(boundingBox: Rect, rotation: Int): Bitmap? {
    var image = this
    if (rotation != 0) {
        val matrix = Matrix()
        matrix.postRotate(rotation.toFloat())
        image = Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
    }
    return if (boundingBox.top >= 0
        && boundingBox.bottom <= image.width
        && boundingBox.top + boundingBox.height() <= image.height
        && boundingBox.left >= 0
        && boundingBox.left + boundingBox.width() <= image.width
    ) {
        Bitmap.createBitmap(
            image,
            boundingBox.left,
            boundingBox.top,
            boundingBox.width(),
            boundingBox.height()
        )
    } else null
}

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
    val croppedBitmap = Bitmap.createBitmap(this, rect.left, rect.top, width, height)
    // Uncomment the below line if you want to save the input image.
    // BitmapUtils.saveBitmap( context , croppedBitmap , "source" )
    return croppedBitmap
}

fun Bitmap.rotateBitmap(degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, false)
}

fun Bitmap.resizedForTfLite(): Bitmap {
    return Bitmap.createScaledBitmap(
        this,
        321,
        321,
        true
    )
}

fun Bitmap.resizedForTfLite112(): Bitmap {
    return Bitmap.createScaledBitmap(
        this,
        112,
        112,
        true
    )
}