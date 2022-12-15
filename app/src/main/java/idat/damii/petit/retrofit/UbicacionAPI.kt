package idat.damii.petit.retrofit

import idat.damii.petit.model.Ubicacion
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UbicacionAPI {
    @GET("api/listaUbicaciones/{idState}")
    fun obtenerEntregas(@Path("idState") idState: Int): Call<List<Ubicacion>>
}