package idat.damii.petit.retrofit

import idat.damii.petit.retrofit.request.Adopcion
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AdopcionAPI {
    @POST("api/users/{userId}/{stateId}/{petId}/createAdoption")
    fun guardarSolicitudAdopcion(
        @Path("userId") userId: Int,
        @Path("stateId") stateId: Int,
        @Path("petId") petId: Int,
        @Body adopcion: Adopcion,
    ): Call<Adopcion>
}