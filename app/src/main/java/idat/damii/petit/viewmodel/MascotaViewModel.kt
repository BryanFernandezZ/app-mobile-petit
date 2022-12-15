package idat.damii.petit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import idat.damii.petit.model.Mascota
import idat.damii.petit.repository.MascotaRepository

class MascotaViewModel: ViewModel() {
    var responseObtenerMascotas: LiveData<List<Mascota>>
    var responseGetMascotas : LiveData<Mascota>

    private var repository = MascotaRepository()

    init {
        responseObtenerMascotas = repository.obtenerMascotas()
        responseGetMascotas = repository.responseGetMascota
    }

    fun obtenerMascotas(){
        responseObtenerMascotas = repository.obtenerMascotas()
    }

    fun getMascota(id: Int) {
        responseGetMascotas = repository.getMascota(id)
    }
}