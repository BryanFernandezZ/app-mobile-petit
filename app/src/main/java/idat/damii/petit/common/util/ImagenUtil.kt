package idat.damii.petit.common.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImagenUtil {
    fun obtenerFotoCodificada(bitmapDrawable: BitmapDrawable): String {
        var bitmap: Bitmap = bitmapDrawable.bitmap
        var baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    fun obtenerImagenDelTextoCodificado(imagenCodificada: String): Bitmap {
        var bytes = Base64.decode(imagenCodificada, Base64.DEFAULT)
        var bitmap: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        return bitmap
    }

}