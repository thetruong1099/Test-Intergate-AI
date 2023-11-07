package com.testintergateai.domain.model

import android.graphics.Bitmap
import android.graphics.Rect

data class FaceAnalyzer(
    val rect: Rect? = null,
    val face: Bitmap? = null,
    val name: String? = null,
    val extra: Array<FloatArray> = arrayOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FaceAnalyzer

        if (rect != other.rect) return false
        if (face != other.face) return false
        if (name != other.name) return false
        if (!extra.contentDeepEquals(other.extra)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rect?.hashCode() ?: 0
        result = 31 * result + (face?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + extra.contentDeepHashCode()
        return result
    }
}
