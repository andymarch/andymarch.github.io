package uk.co.andymarch.blogposter.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ProcessedImage(
    val bytes: ByteArray,
    val mimeType: String,
    val widthPx: Int,
    val heightPx: Int,
)

/**
 * Loads a picked/captured photo, corrects its orientation using its EXIF tag
 * (camera photos are usually stored "sideways" with an orientation flag rather
 * than pre-rotated pixels), downscales it so nobody accidentally commits a
 * 12MB original into the repo, and re-encodes it as a JPEG.
 */
class ImageProcessor(private val context: Context) {

    suspend fun process(uri: Uri, maxDimensionPx: Int, jpegQuality: Int): ProcessedImage =
        withContext(Dispatchers.Default) {
            val orientation = readExifOrientation(uri)
            val decoded = decodeScaledBitmap(uri, maxDimensionPx)
            val oriented = applyOrientation(decoded, orientation)
            val resized = downscaleIfNeeded(oriented, maxDimensionPx)

            val output = ByteArrayOutputStream()
            resized.compress(Bitmap.CompressFormat.JPEG, jpegQuality.coerceIn(1, 100), output)
            val width = resized.width
            val height = resized.height

            decoded.recycle()
            oriented.recycle()
            resized.recycle()

            ProcessedImage(bytes = output.toByteArray(), mimeType = "image/jpeg", widthPx = width, heightPx = height)
        }

    private fun readExifOrientation(uri: Uri): Int =
        context.contentResolver.openInputStream(uri)?.use { input ->
            ExifInterface(input).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        } ?: ExifInterface.ORIENTATION_NORMAL

    private fun decodeScaledBitmap(uri: Uri, maxDimensionPx: Int): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }

        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, maxDimensionPx)
        }
        val stream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Could not open image at $uri")
        return stream.use { BitmapFactory.decodeStream(it, null, options) }
            ?: throw IllegalArgumentException("Could not decode image at $uri")
    }

    private fun calculateInSampleSize(width: Int, height: Int, maxDimension: Int): Int {
        var sampleSize = 1
        var w = width
        var h = height
        while (w / 2 >= maxDimension && h / 2 >= maxDimension) {
            sampleSize *= 2
            w /= 2
            h /= 2
        }
        return sampleSize
    }

    private fun applyOrientation(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.postRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.postRotate(270f)
                matrix.postScale(-1f, 1f)
            }
            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun downscaleIfNeeded(bitmap: Bitmap, maxDimensionPx: Int): Bitmap {
        val longestSide = maxOf(bitmap.width, bitmap.height)
        if (longestSide <= maxDimensionPx) return bitmap
        val scale = maxDimensionPx.toFloat() / longestSide
        val newWidth = (bitmap.width * scale).toInt().coerceAtLeast(1)
        val newHeight = (bitmap.height * scale).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
