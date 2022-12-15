package idat.damii.petit.repository

import android.util.Base64
import android.util.Log
import androidx.lifecycle.MutableLiveData
import idat.damii.petit.common.values.Constantes
import idat.damii.petit.retrofit.request.AccountUserRequest
import idat.damii.petit.retrofit.response.AccountUserResponse
import idat.damii.petit.retrofit.response.LoginResponse
import idat.damii.petit.model.Cuenta
import idat.damii.petit.model.Usuario
import idat.damii.petit.repository.network.PetitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository {
    var loginResponse = MutableLiveData<LoginResponse>()
    var registroResponse = MutableLiveData<Usuario>()
    var cuentaResponse = MutableLiveData<Cuenta>()
    var datosUsuarioResponse = MutableLiveData<List<Usuario>>()
    var datosUsuarioResponsePreLogin = MutableLiveData<List<Usuario>>()
    var cuentaUserResponse = MutableLiveData<AccountUserResponse>()
    var refrescarTokenResponse = MutableLiveData<LoginResponse>()

    fun login(usuario: String, password: String): MutableLiveData<LoginResponse> {
        val base = "${Constantes.serverClientId}:${Constantes.serverClientSecret}"
        val authHeader: String =
            "Basic " + Base64.encodeToString(base.toByteArray(), Base64.NO_WRAP)

        val call: Call<LoginResponse> = PetitClient.cuentaService.login(
            authHeader,
            usuario,
            password,
            Constantes.payloadGrantType
        )

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        loginResponse.value = response.body()
                    }
                } else {
                    val loginResponseVacio: LoginResponse = LoginResponse("", "", "", 0, "", "")
                    loginResponse.value = loginResponseVacio
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("Error Login: ", t.message.toString())
            }
        })

        return loginResponse
    }

    fun registrarUsuario(usuario: Usuario): MutableLiveData<Usuario> {
        val call: Call<Usuario> = PetitClient.usuarioService.registrarUsuario(usuario)
        call.enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful && response.body() != null)
                    registroResponse.value = response.body()
                else if (response.code() == 500) {
                    val usuarioVacio = Usuario(0, "", "", "", "", "")
                    registroResponse.value = usuarioVacio
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Log.e("Error Registro Usuario: ", t.message.toString())
            }
        })

        return registroResponse
    }

    fun registrarCuenta(
        idTipoCuenta: Int,
        idusuario: Int,
        cuenta: Cuenta,
    ): MutableLiveData<Cuenta> {
        val call: Call<Cuenta> =
            PetitClient.cuentaService.registrarCuenta(idTipoCuenta, idusuario, cuenta)
        call.enqueue(object : Callback<Cuenta> {
            override fun onResponse(call: Call<Cuenta>, response: Response<Cuenta>) {
                if (response.isSuccessful && response.body() != null)
                    cuentaResponse.value = response.body()
                else if (response.code() == 500) {
                    val cuenta = Cuenta(0, "", "")
                    cuentaResponse.value = cuenta
                }
            }

            override fun onFailure(call: Call<Cuenta>, t: Throwable) {
                Log.e("Error Registro Cuenta: ", t.message.toString())
            }
        })

        return cuentaResponse
    }

    fun registrarCuentaUsuario(accountUserRequest: AccountUserRequest): MutableLiveData<AccountUserResponse> {
        val call: Call<AccountUserResponse> =
            PetitClient.usuarioService.registrarCuentaUsuario(accountUserRequest)
        call.enqueue(object : Callback<AccountUserResponse> {
            override fun onResponse(
                call: Call<AccountUserResponse>,
                response: Response<AccountUserResponse>,
            ) {
                if (response.isSuccessful && response.body() != null) {
                    cuentaUserResponse.value = response.body()
                }
            }

            override fun onFailure(call: Call<AccountUserResponse>, t: Throwable) {
                Log.e("Error Registrar Cuenta de usuario", t.message.toString())
            }

        })

        return cuentaUserResponse
    }

    fun obtenerDatosUsuario(email: String): MutableLiveData<List<Usuario>> {

        val call: Call<List<Usuario>> = PetitClient.usuarioService.obtenerDatosUsuario(email)

        call.enqueue(object : Callback<List<Usuario>> {
            override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                if (response.isSuccessful && response.body() != null)
                    datosUsuarioResponse.value = response.body()
                else if (response.code() == 401)
                    datosUsuarioResponse.value = listOf(Usuario(0, "", "", "", "", ""))
            }

            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                Log.e("Error Obtener datos de usuario", t.message.toString())
            }

        })

        return datosUsuarioResponse
    }

    fun obtenerDatosUsuarioPreLogin(email: String): MutableLiveData<List<Usuario>> {
        val call: Call<List<Usuario>> =
            PetitClient.usuarioService.obtenerDatosUsuarioPreLogin(email)

        call.enqueue(object : Callback<List<Usuario>> {
            override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                if (response.isSuccessful && response.body() != null)
                    datosUsuarioResponsePreLogin.value = response.body()
                else if (response.code() == 401)
                    datosUsuarioResponsePreLogin.value = listOf(Usuario(0, "", "", "", "", ""))
            }

            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                Log.e("Error Obtener datos de usuario", t.message.toString())
            }
        })

        return datosUsuarioResponsePreLogin
    }

    fun refrescarToken(refreshToken: String): MutableLiveData<LoginResponse> {
        val base = "${Constantes.serverClientId}:${Constantes.serverClientSecret}"
        val authHeader: String =
            "Basic " + Base64.encodeToString(base.toByteArray(), Base64.NO_WRAP)
        val call: Call<LoginResponse> =
            PetitClient.cuentaService.refrescarToken(authHeader, refreshToken, "refresh_token")
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null)
                    refrescarTokenResponse.value = response.body()
                else if (response.code() == 401)
                    refrescarTokenResponse.value = LoginResponse("expired", "", "", 0, "", "")
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("Error al refrescar token", t.message.toString())
            }
        })

        return refrescarTokenResponse
    }
}