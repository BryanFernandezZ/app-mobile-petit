package idat.damii.petit.retrofit

import idat.damii.petit.retrofit.request.CorreoRequest
import idat.damii.petit.retrofit.response.AccountUserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface CorreoAPI {
    @POST("api/v1/email/send")
    fun enviarCorreo(@Body correo: CorreoRequest): Call<AccountUserResponse>
}