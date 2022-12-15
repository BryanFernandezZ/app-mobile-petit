package idat.damii.petit.retrofit

import idat.damii.petit.retrofit.request.AccountUserRequest
import idat.damii.petit.retrofit.response.AccountUserResponse
import idat.damii.petit.model.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UsuarioAPI {
    @POST("public/api/users/create")
    fun registrarUsuario(@Body usuario: Usuario): Call<Usuario>

//    @GET("api/users/email/{email}")
//    fun obtenerDatosUsuario(
//            @Header("Authorization") authHeader: String,
//        @Path("email") email: String
//    ): Call<ArrayList<Usuario>>

    @GET("api/users/email/{email}")
    fun obtenerDatosUsuario(
        @Path("email") email: String
    ): Call<List<Usuario>>

    @GET("public/api/users/email/{email}")
    fun obtenerDatosUsuarioPreLogin(
        @Path("email") email: String
    ): Call<List<Usuario>>

    @POST("public/api/users/createAccountUser")
    fun registrarCuentaUsuario(@Body accountUserRequest: AccountUserRequest): Call<AccountUserResponse>
}