package idat.damii.petit.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import idat.damii.petit.model.Cita
import idat.damii.petit.repository.network.PetitClient
import idat.damii.petit.retrofit.response.AccountUserResponse
import idat.damii.petit.retrofit.response.MiCitasResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CitaRepository {
    var responseGetFechas = MutableLiveData<List<String>>()
    var responseRegistroCita = MutableLiveData<Cita>()
    var responseObtenerMisCitas = MutableLiveData<List<MiCitasResponse>>()
    var responseRegistrarCitaFinal = MutableLiveData<AccountUserResponse>()

    fun obtenerFechas(context: Context): MutableLiveData<List<String>> {
        val call: Call<ArrayList<String>> = PetitClient.citaService.obtenerFechasDisponibles()
        call.enqueue(object : Callback<ArrayList<String>> {
            override fun onResponse(
                call: Call<ArrayList<String>>,
                response: Response<ArrayList<String>>,
            ) {
                responseGetFechas.value = response.body()
            }

            override fun onFailure(call: Call<ArrayList<String>>, t: Throwable) {
                Log.e("Error al obtener fechas: ", t.message.toString())
            }

        })

        return responseGetFechas
    }

    fun registrarCita(
        idTipoServicio: Int,
        idUsuario: Int,
        idEstado: Int,
        cita: Cita,
    ): MutableLiveData<Cita> {
        val call: Call<Cita> =
            PetitClient.citaService.registrarCita(idTipoServicio, idUsuario, idEstado, cita)
        call.enqueue(object : Callback<Cita> {
            override fun onResponse(call: Call<Cita>, response: Response<Cita>) {
                if (response.isSuccessful && response.body() != null)
                    responseRegistroCita.value = response.body()
            }

            override fun onFailure(call: Call<Cita>, t: Throwable) {
                Log.e("Error al registrar citas: ", t.message.toString())
            }

        })

        return responseRegistroCita
    }

    fun registrarCitaFinal(
        idTipoServicio: Int,
        idUsuario: Int,
        idEstado: Int,
        cita: Cita,
    ): MutableLiveData<AccountUserResponse> {
        val call: Call<AccountUserResponse> =
            PetitClient.citaService.registrarCitaFinal(idTipoServicio, idUsuario, idEstado, cita)
        call.enqueue(object : Callback<AccountUserResponse> {
            override fun onResponse(
                call: Call<AccountUserResponse>,
                response: Response<AccountUserResponse>,
            ) {
                if (response.isSuccessful && response.body() != null)
                    responseRegistrarCitaFinal.value = response.body()
            }

            override fun onFailure(call: Call<AccountUserResponse>, t: Throwable) {
                Log.e("Error al registrar citas: ", t.message.toString())
            }
        })

        return responseRegistrarCitaFinal
    }

    fun obtenerMisCitas(id: Int): MutableLiveData<List<MiCitasResponse>> {

        val call: Call<List<MiCitasResponse>> = PetitClient.citaService.obtenerMisCitas(id)
        call.enqueue(object : Callback<List<MiCitasResponse>> {
            override fun onResponse(
                call: Call<List<MiCitasResponse>>,
                response: Response<List<MiCitasResponse>>,
            ) {
                if (response.isSuccessful && response.body() != null)
                    responseObtenerMisCitas.value = response.body()
            }

            override fun onFailure(call: Call<List<MiCitasResponse>>, t: Throwable) {
                Log.e("Error al obtener mis citas: ", t.message.toString())
            }

        })
        return responseObtenerMisCitas
    }
}