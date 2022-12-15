package idat.damii.petit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import idat.damii.petit.repository.AdopcionRepository
import idat.damii.petit.retrofit.request.Adopcion

class AdopcionViewModel : ViewModel() {
    var responseGuardarSolicitudAdopcion: LiveData<Adopcion>

    private var repository = AdopcionRepository()

    init {
        responseGuardarSolicitudAdopcion = repository.responseGuardarSolicitudAdopcion
    }

    fun guardarSolicitudAdopcion(
        userId: Int,
        stateId: Int,
        petId: Int,
        adopcion: Adopcion,
    ) {
        responseGuardarSolicitudAdopcion =
            repository.guardarSolicitudAdopcion(userId, stateId, petId, adopcion)
    }
}