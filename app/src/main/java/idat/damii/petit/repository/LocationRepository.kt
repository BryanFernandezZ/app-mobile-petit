package idat.damii.petit.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import idat.damii.petit.repository.network.PetitClient
import idat.damii.petit.retrofit.request.LocationRequest
import idat.damii.petit.retrofit.response.AccountUserResponse
import idat.damii.petit.retrofit.response.LocationResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationRepository {
    var responseGuardarUbicacion = MutableLiveData<LocationResponse>()
    var responseObtenerUbicacion = MutableLiveData<LocationResponse>()

    fun guardarUbicacion(id: Int, location: LocationRequest): MutableLiveData<LocationResponse> {
        val call: Call<LocationResponse> =
            PetitClient.locationService.guardarUbicacion(id, location)
        call.enqueue(object : Callback<LocationResponse> {
            override fun onResponse(
                call: Call<LocationResponse>,
                response: Response<LocationResponse>,
            ) {
                if (response.isSuccessful && response.body() != null) {
                    responseGuardarUbicacion.value = response.body()
                }
            }

            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                Log.e("Error al guardar ubicacion", t.message.toString())
            }

        })
        return responseGuardarUbicacion
    }

    fun obtenerUbicacion(id: Int): MutableLiveData<LocationResponse> {
        val call: Call<LocationResponse> =
            PetitClient.locationService.obtenerUbicacionUsuario(id)
        call.enqueue(object : Callback<LocationResponse> {
            override fun onResponse(
                call: Call<LocationResponse>,
                response: Response<LocationResponse>,
            ) {
                if (response.isSuccessful)
                    responseObtenerUbicacion.value = response.body()
            }

            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                Log.e("Error al encontrar ubicacion", t.message.toString())
            }

        })
        return responseObtenerUbicacion
    }
}