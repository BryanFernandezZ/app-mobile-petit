package idat.damii.petit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import idat.damii.petit.repository.CorreoRepository
import idat.damii.petit.retrofit.request.CorreoRequest
import idat.damii.petit.retrofit.response.AccountUserResponse

class CorreoViewModel : ViewModel() {
    var enviarCorreoResponse: LiveData<AccountUserResponse>

    private var repository: CorreoRepository = CorreoRepository()

    init {
        enviarCorreoResponse = repository.enviarCorreoResponse
    }

    fun enviarCorreo(correo: CorreoRequest) {
        enviarCorreoResponse = repository.enviarCorreo(correo)
    }
}