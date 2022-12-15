package idat.damii.petit.view

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import com.facebook.Profile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import idat.damii.petit.common.util.ImagenUtil
import idat.damii.petit.common.util.MensajeUtil
import idat.damii.petit.common.values.App
import idat.damii.petit.common.values.Constantes
import idat.damii.petit.databinding.ActivityRegisterProveedorBinding
import idat.damii.petit.model.Usuario
import idat.damii.petit.retrofit.request.AccountUserRequest
import idat.damii.petit.viewmodel.AuthViewModel

class RegisterProveedorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterProveedorBinding
    private lateinit var accountUserRequest: AccountUserRequest
    private lateinit var authViewModel: AuthViewModel
    private lateinit var alertDialog: AlertDialog
    private lateinit var app: App
    private var cuenta: String = ""
    private var urlFoto = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterProveedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = applicationContext as App

        cargarDatosRegistroProveedor()

        alertDialog = AlertDialog.Builder(this)
            .setTitle("Guardando")
            .setMessage("Espere por favor...").create()

        authViewModel = ViewModelProvider(this)
            .get(AuthViewModel::class.java)

        authViewModel.responseCuentaUsuarioRegistro.observe(this, Observer { response ->
            if (response.status == 201) {
                alertDialog.dismiss()
                MensajeUtil.enviarMensaje(binding.root, response.message)
                app.usuario = Usuario(
                    response.body?.idUser,
                    response.body?.names!!,
                    response.body?.lastNames!!,
                    response.body?.dni!!,
                    response.body?.phone!!,
                    response.body?.photo!!,
                )
                authViewModel.login(response.body?.email!!, response.body?.password!!)
            } else {
                alertDialog.dismiss()
                MensajeUtil.enviarMensaje(binding.root, response.message)
            }
        })

        authViewModel.responseLogin.observe(this, Observer { response ->
            if (response.expires_in == 0) MensajeUtil.enviarMensaje(
                binding.root,
                "Usuario y/o contraseña incorrecta"
            )
            else {
                guardarSesion(
                    accountUserRequest.email,
                    accountUserRequest.password,
                    response.access_token,
                    response.refresh_token
                )
            }
        })

        binding.btnRegisterProveedorContinue.setOnClickListener {
            continuarRegistro()
        }
    }

    private fun guardarSesion(
        username: String,
        password: String,
        accessToken: String,
        refreshToken: String,
    ) {
        var preferences: SharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE)
        var editor: SharedPreferences.Editor = preferences.edit()

        editor.putString("usuario", username)
        editor.putString("contrasenia", password)
        editor.putString("token", accessToken)
        editor.putString("refresh_token", refreshToken)
        editor.putString("tipo_cuenta", cuenta)

        editor.commit()

        irAMenuPrincipal()
    }

    private fun cargarDatosRegistroProveedor() {
        val bundle: Bundle? = intent.extras

        val tipo_cuenta = bundle?.getInt("tipo_cuenta")
        val nombre = bundle?.getString("nombre")
        val apellido = bundle?.getString("apellido")
        val foto = bundle?.getString("foto")
        val email = bundle?.getString("email")
        val password = bundle?.getString("password")

        urlFoto = foto.toString()

        Glide.with(this)
            .load(foto!!)
            .into(binding.imvProfilePicture)

        accountUserRequest = AccountUserRequest(
            null, nombre.toString(),
            apellido.toString(), "", "", foto, null, email.toString(), password.toString(), 2
        )

        if (tipo_cuenta == 3) {
            cuenta = "FACEBOOK"
            obtenerInformacionDelUsuarioConFacebook()
        } else cuenta = "GOOGLE"
    }

    private fun continuarRegistro() {
        if (validarFormulario()) {
            if (!Constantes.PHONE_PATTERN.matcher(
                    binding.registerProveedorPhone.editText?.text.toString().trim()
                ).matches() ||
                !Constantes.PHONE_PATTERN.matcher(
                    binding.registerProveedorDni.editText?.text.toString().trim()
                ).matches()
            ) {
                MensajeUtil.enviarMensaje(
                    binding.root,
                    "Alguno de los datos proporcionados es invalido"
                )
            } else registrarCuenta()
        }
    }

    private fun registrarCuenta() {
        val foto =
            ImagenUtil.obtenerFotoCodificada(binding.imvProfilePicture.drawable as BitmapDrawable)
        accountUserRequest.photo = foto
        accountUserRequest.dni = binding.registerProveedorDni.editText?.text.toString().trim()
        accountUserRequest.phone = binding.registerProveedorPhone.editText?.text.toString().trim()
        authViewModel.registrarCuentaUser(accountUserRequest)
    }

    private fun irAMenuPrincipal() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun validarFormulario(): Boolean {
        var salida = false

        val telefono = binding.registerProveedorPhone
        val dni = binding.registerProveedorDni

        if (telefono.editText?.text.toString()
                .trim() != "" && dni.editText?.text.toString().trim() != ""
        ) {
            telefono.error = null
            dni.error = null
            salida = true
        } else {
            if (telefono.editText?.text.toString()
                    .trim() == "" && dni.editText?.text.toString().trim() == ""
            ) {
                telefono.error = "*Este campo es obligatorio*"
                dni.error = "*Este campo es obligatorio*"
                MensajeUtil.enviarMensaje(binding.root, "Todos los campos son obligatorios")
            } else if (telefono.editText?.text.toString().trim() != "") {
                telefono.error = null
                dni.error = "*Este campo es obligatorio*"
            } else if (dni.editText?.text.toString().trim() != "") {
                telefono.error = "*Este campo es obligatorio*"
                dni.error = null
            }
        }

        return salida
    }

    private fun obtenerInformacionDelUsuarioConFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) irALogin()
        else {
            var profile: Profile? = Profile.getCurrentProfile()

            if (profile != null) {
                accountUserRequest.names = profile.firstName.toString()
                accountUserRequest.lastNames = profile.lastName.toString()
                accountUserRequest.password = profile.id.toString()
                accountUserRequest.idAccountType = 3
            }
        }
    }

    private fun irALogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}