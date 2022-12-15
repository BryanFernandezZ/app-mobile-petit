package idat.damii.petit.retrofit

import idat.damii.petit.model.Cita
import idat.damii.petit.retrofit.response.AccountUserResponse
import idat.damii.petit.retrofit.response.MiCitasResponse
import retrofit2.Call
import retrofit2.http.*

interface CitaAPI {
    @GET("api/countQuotes")
    fun obtenerFechasDisponibles(): Call<ArrayList<String>>

    @POST("api/users/{idTipoServicio}/{idUsuario}/{idEstado}/createQuote")
    fun registrarCita(
        @Path("idTipoServicio") idTipoServicio: Int, @Path("idUsuario") idUsuario: Int,
        @Path("idEstado") idEstado: Int, @Body cita: Cita
    ): Call<Cita>

    @POST("api/users/{idTipoServicio}/{idUsuario}/{idEstado}/createQuote")
    fun registrarCitaFinal(
        @Path("idTipoServicio") idTipoServicio: Int, @Path("idUsuario") idUsuario: Int,
        @Path("idEstado") idEstado: Int, @Body cita: Cita
    ): Call<AccountUserResponse>

    @GET("api/users/{userId}/listQuotes")
    fun obtenerMisCitas(@Path("userId") userId: Int): Call<List<MiCitasResponse>>
}