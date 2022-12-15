package idat.damii.petit.common.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

object MensajeUtil {
    fun enviarMensaje(view: View, mensaje: String) {
        val snackbar = Snackbar.make(view, mensaje, Snackbar.LENGTH_LONG)
        snackbar.show()
    }
}