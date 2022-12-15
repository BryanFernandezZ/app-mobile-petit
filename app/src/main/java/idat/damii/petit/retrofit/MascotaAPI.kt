package idat.damii.petit.retrofit

import idat.damii.petit.model.Mascota
import retrofit2.Call
import retrofit2.http.*

interface MascotaAPI {
    @GET("api/pets/listPets")
    fun obtenerMascotas(): Call<List<Mascota>>

    @GET("api/pets/{id}")
    fun getMascota(@Path("id") id: Int): Call<Mascota>
}