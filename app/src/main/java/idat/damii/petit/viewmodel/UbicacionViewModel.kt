package idat.damii.petit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import idat.damii.petit.model.Ubicacion
import idat.damii.petit.repository.UbicacionRepository

class UbicacionViewModel : ViewModel() {
    var responseUbicaciones: LiveData<List<Ubicacion>>

    private var repository = UbicacionRepository()

    init {
        responseUbicaciones = repository.responseUbicaciones
    }

    fun obtenerUbicaciones(idState: Int) {
        responseUbicaciones = repository.obtenerUbicaciones(idState)
    }
}