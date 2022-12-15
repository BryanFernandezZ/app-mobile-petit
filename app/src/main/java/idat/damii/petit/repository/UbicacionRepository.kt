package idat.damii.petit.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import idat.damii.petit.model.Ubicacion
import idat.damii.petit.repository.network.PetitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UbicacionRepository {
    val responseUbicaciones = MutableLiveData<List<Ubicacion>>()

    fun obtenerUbicaciones(idState: Int): MutableLiveData<List<Ubicacion>> {
        val call: Call<List<Ubicacion>> = PetitClient.ubicacionService.obtenerEntregas(idState)
        call.enqueue(object : Callback<List<Ubicacion>> {
            override fun onResponse(call: Call<List<Ubicacion>>, response: Response<List<Ubicacion>>) {
                if (response.isSuccessful && response.body() != null)
                    responseUbicaciones.value = response.body()
            }

            override fun onFailure(call: Call<List<Ubicacion>>, t: Throwable) {
                Log.e("Error al obtener ubicaciones", t.message.toString())
            }

        })
        return responseUbicaciones
    }
}