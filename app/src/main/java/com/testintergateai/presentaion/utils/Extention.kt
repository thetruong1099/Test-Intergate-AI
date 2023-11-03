package com.testintergateai.presentaion.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import com.google.mlkit.vision.face.Face

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

fun Bitmap.cropToBox(boundingBox: Rect, rotation: Int): Bitmap? {
    var image = this
    val shift = 0
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
            boundingBox.top + shift,
            boundingBox.width(),
            boundingBox.height()
        )
    } else null
}

fun Bitmap.resizedForTfLite(): Bitmap {
    return Bitmap.createScaledBitmap(
        this,
        321,
        321,
        true
    )
}