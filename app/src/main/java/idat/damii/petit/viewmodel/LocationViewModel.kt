package idat.damii.petit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import idat.damii.petit.repository.LocationRepository
import idat.damii.petit.retrofit.request.LocationRequest
import idat.damii.petit.retrofit.response.AccountUserResponse
import idat.damii.petit.retrofit.response.LocationResponse

class LocationViewModel : ViewModel() {
    var responseGuardarUbicacion: LiveData<LocationResponse>
    var responseObtenerUbicacion: LiveData<LocationResponse>

    private var repository = LocationRepository()

    init {
        responseGuardarUbicacion = repository.responseGuardarUbicacion
        responseObtenerUbicacion = repository.responseObtenerUbicacion
    }

    fun guardarUbicacion(id: Int, location: LocationRequest) {
        responseGuardarUbicacion = repository.guardarUbicacion(id, location)
    }

    fun obtenerUbicacion(id: Int) {
        responseObtenerUbicacion = repository.obtenerUbicacion(id)
    }

}