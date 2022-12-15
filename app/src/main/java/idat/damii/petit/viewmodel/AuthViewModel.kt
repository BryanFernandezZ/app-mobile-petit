package idat.damii.petit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import idat.damii.petit.retrofit.response.LoginResponse
import idat.damii.petit.retrofit.request.AccountUserRequest
import idat.damii.petit.retrofit.response.AccountUserResponse
import idat.damii.petit.model.Cuenta
import idat.damii.petit.model.Usuario
import idat.damii.petit.repository.AuthRepository

class AuthViewModel : ViewModel() {

    var responseLogin: LiveData<LoginResponse>
    var responseRegistro: LiveData<Usuario>
    var responseCuenta: LiveData<Cuenta>
    var responseDatosUsuario: LiveData<List<Usuario>>
    var responseDatosUsuarioPreLogin : LiveData<List<Usuario>>
    var responseCuentaUsuarioRegistro: LiveData<AccountUserResponse>
    var responseRefrescarToken: LiveData<LoginResponse>

    private var repository = AuthRepository()

    init {
        responseLogin = repository.loginResponse
        responseRegistro = repository.registroResponse
        responseCuenta = repository.cuentaResponse
        responseDatosUsuario = repository.datosUsuarioResponse
        responseDatosUsuarioPreLogin = repository.datosUsuarioResponsePreLogin
        responseCuentaUsuarioRegistro = repository.cuentaUserResponse
        responseRefrescarToken = repository.refrescarTokenResponse
    }

    fun login(usuario: String, password: String) {
        responseLogin = repository.login(usuario, password)

    }

    fun registrarUsuario(
        names: String,
        lastNames: String,
        dni: String,
        phone: String,
        photo: String
    ) {
        responseRegistro =
            repository.registrarUsuario(Usuario(null, names, lastNames, dni, phone, photo))
    }

    fun registrarCuenta(
        idTipoCuenta: Int,
        idUsuario: Int,
        cuenta: Cuenta
    ) {
        responseCuenta = repository.registrarCuenta(idTipoCuenta, idUsuario, cuenta)
    }

    fun obtenerDatosUsuario(email: String){
        responseDatosUsuario = repository.obtenerDatosUsuario(email)
    }

    fun obtenerDatosUsuarioPreLogin(email: String){
        responseDatosUsuarioPreLogin = repository.obtenerDatosUsuarioPreLogin(email)
    }

    fun registrarCuentaUser(accountUser: AccountUserRequest) {
        responseCuentaUsuarioRegistro = repository.registrarCuentaUsuario(accountUser)
    }

    fun refrescarToken(refreshToken: String) {
        responseRefrescarToken = repository.refrescarToken(refreshToken)
    }
}