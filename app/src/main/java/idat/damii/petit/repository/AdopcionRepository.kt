package idat.damii.petit.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import idat.damii.petit.repository.network.PetitClient
import idat.damii.petit.retrofit.request.Adopcion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdopcionRepository {
    var responseGuardarSolicitudAdopcion = MutableLiveData<Adopcion>()

    fun guardarSolicitudAdopcion(
        userId: Int,
        stateId: Int,
        petId: Int,
        adopcion: Adopcion,
    ): MutableLiveData<Adopcion> {
        val call: Call<Adopcion> = PetitClient.adopcionService.guardarSolicitudAdopcion(userId,
            stateId,
            petId,
            adopcion)
        call.enqueue(object : Callback<Adopcion> {
            override fun onResponse(call: Call<Adopcion>, response: Response<Adopcion>) {
                if (response.isSuccessful)
                    responseGuardarSolicitudAdopcion.value = response.body()
            }

            override fun onFailure(call: Call<Adopcion>, t: Throwable) {
                Log.e("Error al guardar solicitud adopcion", t.message.toString())
            }

        })
        return responseGuardarSolicitudAdopcion
    }
}