package idat.damii.petit.retrofit

import idat.damii.petit.retrofit.response.LoginResponse
import idat.damii.petit.model.Cuenta
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface CuentaAPI {
    @POST("public/api/users/account/{idcuenta}/{idusuario}/createAccount")
    fun registrarCuenta(
        @Path("idcuenta") idcuenta: Int,
        @Path("idusuario") idusuario: Int,
        @Body cuenta: Cuenta
    ): Call<Cuenta>

    @POST("oauth/token")
    @FormUrlEncoded
    fun login(
        @Header("Authorization") authHeader: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String
    ): Call<LoginResponse>

    @POST("oauth/token")
    @FormUrlEncoded
    fun refrescarToken(
        @Header("Authorization") authHeader: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String,
    ): Call<LoginResponse>
}