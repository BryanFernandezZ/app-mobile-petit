package idat.damii.petit.common.values

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import idat.damii.petit.model.Usuario
import idat.damii.petit.repository.network.PetitClient
import idat.damii.petit.viewmodel.AuthViewModel

class App : Application() {
    lateinit var usuario: Usuario
    var logout: Boolean = false
    var urlFoto: String = ""
    var idUsuario: Int = 0
}