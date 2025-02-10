package com.valify.registrationsdk.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import java.io.ByteArrayOutputStream

object ImageUtils {
    private const val TAG = "ImageUtils"
    private const val DEFAULT_QUALITY = 80
    private const val MAX_DIMENSION = 1080

    fun compressAndOptimize(
        bitmap: Bitmap,
        quality: Int = DEFAULT_QUALITY,
        maxDimension: Int = MAX_DIMENSION
    ): ByteArray {
        var resultBitmap = bitmap
        
        try {
            if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
                val scale = maxDimension.toFloat() / maxOf(bitmap.width, bitmap.height)
                resultBitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    (bitmap.width * scale).toInt(),
                    (bitmap.height * scale).toInt(),
                    true
                )
            }

            val outputStream = ByteArrayOutputStream()
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            return outputStream.toByteArray()
        } catch (e: Exception) {
            Log.e(TAG, "Error compressing image", e)
            throw e
        } finally {
            if (resultBitmap != bitmap) {
                resultBitmap.recycle()
            }
        }
    }

    fun transformBitmap(
        bitmap: Bitmap,
        rotation: Float,
        flipHorizontal: Boolean = false
    ): Bitmap {
        val matrix = Matrix()
        
        if (rotation != 0f) {
            matrix.postRotate(rotation)
        }
        
        if (flipHorizontal) {
            matrix.postScale(-1f, 1f)
        }
        
        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    fun recycleBitmap(bitmap: Bitmap?) {
        try {
            if (bitmap != null && !bitmap.isRecycled) {
                bitmap.recycle()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error recycling bitmap", e)
        }
    }
}
