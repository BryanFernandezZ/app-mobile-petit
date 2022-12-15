package idat.damii.petit.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import idat.damii.petit.model.Mascota
import idat.damii.petit.repository.network.PetitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.crypto.Mac

class MascotaRepository {
    var resposeObtenerMascotas = MutableLiveData<List<Mascota>>()
    var responseGetMascota = MutableLiveData<Mascota>()

    fun obtenerMascotas(): MutableLiveData<List<Mascota>> {
        val call: Call<List<Mascota>> = PetitClient.mascotaService.obtenerMascotas()
        call.enqueue(object : Callback<List<Mascota>> {
            override fun onResponse(call: Call<List<Mascota>>, response: Response<List<Mascota>>) {
                if (response.isSuccessful && response.body() != null)
                    resposeObtenerMascotas.value = response.body()
            }

            override fun onFailure(call: Call<List<Mascota>>, t: Throwable) {
                Log.e("Error al obtener mascotas", t.message.toString())
            }
        })

        return resposeObtenerMascotas
    }

    fun getMascota(id: Int): MutableLiveData<Mascota> {
        val call: Call<Mascota> = PetitClient.mascotaService.getMascota(id)
        call.enqueue(object : Callback<Mascota> {
            override fun onResponse(call: Call<Mascota>, response: Response<Mascota>) {
                if (response.isSuccessful && response.body() != null)
                    responseGetMascota.value = response.body()
            }

            override fun onFailure(call: Call<Mascota>, t: Throwable) {
                Log.e("Error al obtener mascotal", t.message.toString())
            }

        })
        return responseGetMascota
    }
}