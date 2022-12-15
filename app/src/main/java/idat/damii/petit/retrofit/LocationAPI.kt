package idat.damii.petit.retrofit

import idat.damii.petit.retrofit.request.LocationRequest
import idat.damii.petit.retrofit.response.AccountUserResponse
import idat.damii.petit.retrofit.response.LocationResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LocationAPI {
    @POST("api/v1/location/create/{id}")
    fun guardarUbicacion(
        @Path("id") id: Int,
        @Body location: LocationRequest,
    ): Call<LocationResponse>

    @GET("api/v1/location/user/{id}")
    fun obtenerUbicacionUsuario(@Path("id") id: Int): Call<LocationResponse>
}