package idat.damii.petit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import idat.damii.petit.model.Cita
import idat.damii.petit.repository.CitaRepository
import idat.damii.petit.retrofit.response.AccountUserResponse
import idat.damii.petit.retrofit.response.MiCitasResponse

class CitaViewModel : ViewModel() {
    var responseGetFechas: LiveData<List<String>>
    var responseRegistroCita: LiveData<Cita>
    var responseObtenerMisCitas: LiveData<List<MiCitasResponse>>
    var responseRegistrarCitaFinal: LiveData<AccountUserResponse>

    private var repository = CitaRepository()


    init {
        responseGetFechas = repository.responseGetFechas
        responseRegistroCita = repository.responseRegistroCita
        responseObtenerMisCitas = repository.responseObtenerMisCitas
        responseRegistrarCitaFinal = repository.responseRegistrarCitaFinal
    }

    fun getFechas(context: Context) {
        responseGetFechas = repository.obtenerFechas(context)
    }

    fun registrarCita(idTipoServicio: Int, idUsuario: Int, idEstado: Int, cita: Cita) {
        responseRegistroCita = repository.registrarCita(idTipoServicio, idUsuario, idEstado, cita)
    }

    fun obtenerMisCitas(idUsuario: Int) {
        responseObtenerMisCitas = repository.obtenerMisCitas(idUsuario)
    }

    fun registrarCitaFinal(idTipoServicio: Int, idUsuario: Int, idEstado: Int, cita: Cita) {
        responseRegistrarCitaFinal =
            repository.registrarCitaFinal(idTipoServicio, idUsuario, idEstado, cita)
    }
}