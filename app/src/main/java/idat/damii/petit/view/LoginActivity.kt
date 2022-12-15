package idat.damii.petit.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import idat.damii.petit.common.values.App
import idat.damii.petit.R
import idat.damii.petit.databinding.ActivityLoginBinding
import idat.damii.petit.common.util.MensajeUtil
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.OnCompleteListener
import de.hdodenhof.circleimageview.CircleImageView
import idat.damii.petit.common.util.AppPreferences
import idat.damii.petit.common.util.ImagenUtil
import idat.damii.petit.model.Usuario
import idat.damii.petit.retrofit.request.AccountUserRequest
import idat.damii.petit.retrofit.response.LoginResponse
import idat.damii.petit.viewmodel.AuthViewModel
import org.json.JSONObject

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var app: App
    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var authViewModel: AuthViewModel
    private lateinit var accountUserRequest: AccountUserRequest
    private lateinit var requestQueue: RequestQueue

    private var RC_SIGN_IN = 181818
    private var FB_SIGN_IN = 202020
    private var widthFoto = 0
    private var heigthFoto = 0

    //Facebook
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.SpashTheme)
        Thread.sleep(2000)
        setTheme(R.style.Theme_Petit)

        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppPreferences.onCreate(this)

        app = applicationContext as App

        gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
                .requestEmail()
                .requestProfile()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnLogin.setOnClickListener(this)
        binding.btnRegistrarse.setOnClickListener(this)
        binding.imvGoogleLogin.setOnClickListener(this)
        binding.imvFacebookLogin.setOnClickListener(this)

        requestQueue = Volley.newRequestQueue(applicationContext)

        authViewModel = ViewModelProvider(this)
            .get(AuthViewModel::class.java)

        authViewModel.responseDatosUsuario.observe(this, Observer { response ->
            validarResponseDatosUsuario(response)
        })

        authViewModel.responseDatosUsuarioPreLogin.observe(this, Observer { response ->
            validarResponseDatosUsuarioPreLogin(response)
        })

        authViewModel.responseLogin.observe(this, Observer { response ->
            validarResponseLogin(response)
        })

        authViewModel.responseRefrescarToken.observe(this, Observer { response ->
            if (!response.access_token.equals("expired")) {
                actualizarToken(response.access_token)
                authViewModel.obtenerDatosUsuario(AppPreferences.getSharedPreferences()
                    ?.getString("usuario", "")!!)
            } else {
                limpiarSesion()
                Toast.makeText(this, "Tu sesion ha expirado", Toast.LENGTH_SHORT).show()
                Log.e("Segundo TOKEN", "Segundo token expirado")
            }
        })
    }

    private fun limpiarSesion() {
        val preferences: SharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()

        editor.remove("usuario")
        editor.remove("contrasenia")
        editor.remove("tipo_cuenta")
        editor.remove("token")
        editor.remove("refresh_token")

        editor.commit()
    }

    private fun actualizarToken(accessToken: String) {
        val preferences: SharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()

        editor.remove("token")
        editor.putString("token", accessToken)
        editor.commit()
    }

    private fun validarResponseLogin(response: LoginResponse) {
        val preferences: SharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()

        if (response.expires_in == 0) MensajeUtil.enviarMensaje(
            binding.root,
            "Usuario y/o contraseña incorrecta"
        )
        else {
            if (accountUserRequest.idAccountType == 1) {
                editor.putString("tipo_cuenta", "ESTANDAR")
                editor.commit()
                guardarSesion(
                    binding.loginUsername.editText?.text.toString().trim(),
                    binding.loginPassword.editText?.text.toString().trim(),
                    response.access_token,
                    response.refresh_token
                )
            } else {
                guardarSesion(
                    accountUserRequest.email,
                    accountUserRequest.password,
                    response.access_token,
                    response.refresh_token
                )
            }
        }
    }

    private fun validarResponseDatosUsuarioPreLogin(response: List<Usuario>) {
        val preferences: SharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()

        if (response.isEmpty()) registrarCuentaProveedor()
        else {
            if (response[0].id == 0) {
                authViewModel.refrescarToken(preferences.getString("refresh_token",
                    "")!!)
            } else {
                app.usuario = response[0]
                when (accountUserRequest.idAccountType) {
                    181818 -> {
                        editor.putString("tipo_cuenta", "GOOGLE")
                        editor.commit()
                        authViewModel.login(accountUserRequest.email, accountUserRequest.password)
                    }

                    202020 -> {
                        editor.putString("tipo_cuenta", "FACEBOOK")
                        editor.commit()
                        authViewModel.login(accountUserRequest.email, accountUserRequest.password)
                    }
                    else -> MensajeUtil.enviarMensaje(binding.root, "Algo ocurrio mal")
                }
            }
        }
    }

    private fun validarResponseDatosUsuario(response: List<Usuario>) {
        val preferences: SharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()

        if (response[0].id == 0) {
            Log.e("PRIMER EXPIRADO", "El primer token ha expirado")
            Toast.makeText(this, "Primer token expirado", Toast.LENGTH_SHORT).show()
            authViewModel.refrescarToken(preferences.getString("refresh_token",
                "")!!)
        } else {
            if (response.isEmpty()) registrarCuentaProveedor()
            else {
                app.usuario = response[0]
                when (accountUserRequest.idAccountType) {
                    181818 -> {
                        editor.putString("tipo_cuenta", "GOOGLE")
                        editor.commit()
                        irAHome()
                    }

                    202020 -> {
                        editor.putString("tipo_cuenta", "FACEBOOK")
                        editor.commit()
                        irAHome()
                    }
                    0 -> {
                        irAHome()
                    }
                    else -> MensajeUtil.enviarMensaje(binding.root, "Algo ocurrio mal")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        app.urlFoto = ""
        cargarSesion()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnLogin -> inciarSesionEstandar()
            R.id.btnRegistrarse -> irARegistro()
            R.id.imvGoogleLogin -> iniciarSesionGoogle()
            R.id.imvFacebookLogin -> iniciarSesionFacebook()
        }
    }

    private fun cargarSesion() {
        var preferences: SharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE)

        when (preferences.getString("tipo_cuenta", "")) {
            "ESTANDAR" -> cargarSesionEstandar()
            "GOOGLE" -> cargarSesionGoogle()
            "FACEBOOK" -> cargarSesionFacebook()
        }
    }

    private fun cargarSesionFacebook() {
        //Cuenta Facebook
        val accessToken: AccessToken? = AccessToken.getCurrentAccessToken()
        val isLoggedIn: Boolean = accessToken != null && !accessToken.isExpired

        if (isLoggedIn) {
            irAHome()
        }
    }

    private fun cargarSesionGoogle() {
        if (app.logout) cerrarSesionGoogle()

        var account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)
    }

    private fun cargarSesionEstandar() {
        var preferences: SharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE)

        var usuario: String? = preferences.getString("usuario", "user")
        var password: String? = preferences.getString("contrasenia", "pass")

        accountUserRequest = AccountUserRequest(
            null,
            "",
            "",
            "",
            "",
            "",
            null,
            usuario.toString(),
            password.toString(),
            0
        )

        authViewModel.obtenerDatosUsuario(usuario!!)

//        if (!(usuario.equals("user")) && !(password.equals("pass"))) {
//            irAHome()
//        }
    }

    private fun cerrarSesionGoogle() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, OnCompleteListener {
            MensajeUtil.enviarMensaje(binding.root, "Cerraste sesion")
        })
    }

    private fun inciarSesionEstandar() {
        if (validarFormulario()) {
            autenticarUsuario(binding.loginUsername, binding.loginPassword)
        }
    }

    private fun iniciarSesionGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun iniciarSesionFacebook() {
        var preferences: SharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE)
        var editor: SharedPreferences.Editor = preferences.edit()

        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    MensajeUtil.enviarMensaje(binding.root, "El proceso ha sido cancelado")
                }

                override fun onError(error: FacebookException) {
                    MensajeUtil.enviarMensaje(binding.root, "Algo ocurrio mal")
                }

                override fun onSuccess(result: LoginResult) {
                    result.let {
                        obtenerOtrosDatosDeFacebook(AccessToken.getCurrentAccessToken())
                    }
                }
            })
    }

    private fun obtenerOtrosDatosDeFacebook(currentAccessToken: AccessToken?) {
        var graphRequest: GraphRequest = GraphRequest.newMeRequest(
            currentAccessToken,
            object : GraphRequest.GraphJSONObjectCallback {
                override fun onCompleted(obj: JSONObject?, response: GraphResponse?) {
                    if (response?.error != null) {
                        MensajeUtil.enviarMensaje(
                            binding.root,
                            response.error!!.errorMessage.toString()
                        )
                        return
                    }
                    val profile = Profile.getCurrentProfile()

                    var email = obj?.getString("email")  //Email
                    val urlFoto: String = obj?.getJSONObject("picture")?.getJSONObject("data")
                        ?.getString("url")!! // Foto

                    app.urlFoto = urlFoto

                    if (email != null && urlFoto != null && profile != null) {
                        cargarUsuarioTemporal(
                            "",
                            "",
                            urlFoto,
                            email,
                            profile.id.toString(),
                            FB_SIGN_IN
                        )
                        validarExistenciaDeUsuario(email)
                    }
                }
            })

        val parameters = Bundle()
        parameters.putString("fields", "id,name,email,picture")
        graphRequest.parameters = parameters
        graphRequest.executeAsync()
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            cargarUsuarioTemporal(
                account.givenName!!,
                account.familyName!!,
                account.photoUrl.toString(),
                account.email!!,
                account.id!!.toString(),
                RC_SIGN_IN
            )
            app.urlFoto = account.photoUrl.toString()
            validarExistenciaDeUsuario(account.email.toString())
        }
    }

    //private fun cargarUsuarioTemporal(account: GoogleSignInAccount) {
    private fun cargarUsuarioTemporal(
        nombre: String,
        apellido: String,
        foto: String,
        email: String,
        password: String,
        idTipoCuenta: Int,
    ) {
        accountUserRequest = AccountUserRequest(
            null,
            nombre,
            apellido,
            "",
            "",
            foto,
            null,
            email,
            password,
            idTipoCuenta
        )
    }

    private fun registrarCuentaProveedor() {
        val intent = Intent(this, RegisterProveedorActivity::class.java).apply {
            putExtra("nombre", accountUserRequest.names)
            putExtra("apellido", accountUserRequest.lastNames)
            putExtra("foto", accountUserRequest.photo)
            putExtra("email", accountUserRequest.email)
            putExtra("password", accountUserRequest.password)

            if (accountUserRequest.idAccountType == RC_SIGN_IN) putExtra("tipo_cuenta", 2)
            else if (accountUserRequest.idAccountType == FB_SIGN_IN) putExtra("tipo_cuenta", 3)
        }

        startActivity(intent)
        finish()
    }

    private fun validarExistenciaDeUsuario(email: String) {
        val preferences = AppPreferences.getSharedPreferences()!!
        val token = preferences.getString("token", "")
        val refreshToken = preferences.getString("refresh_token", "")
        Log.e("Refresh Token", refreshToken.toString())
        Log.e("Access Token", token.toString())
        if (token == "") authViewModel.obtenerDatosUsuarioPreLogin(
            email)
        else authViewModel.obtenerDatosUsuario(email)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            var task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            var account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            updateUI(account)
        } catch (e: ApiException) {
            Log.e("Error", "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun validarFormulario(): Boolean {
        val username = binding.loginUsername
        val password = binding.loginPassword
        var salida = false

        if (username.editText?.text.toString().trim() == "" && password.editText?.text.toString()
                .trim() == ""
        ) {
            username.error = "*Este campo es obligatorio*"
            password.error = "*Este campo es obligatorio*"
            MensajeUtil.enviarMensaje(binding.root, "Todos los campos son obligatorios")
        } else if (username.editText?.text.toString().trim() == "") {
            username.error = "*Este campo es obligatorio*"
            password.error = null
        } else if (password.editText?.text.toString().trim() == "") {
            password.error = "*Este campo es obligatorio*"
            username.error = null
        } else {
            username.error = null
            password.error = null
            salida = true
        }

        return salida
    }

    private fun autenticarUsuario(usuario: TextInputLayout, password: TextInputLayout) {
        accountUserRequest = AccountUserRequest(
            null,
            "",
            "",
            "",
            "",
            "",
            null,
            "",
            "",
            0
        )

        accountUserRequest.idAccountType = 1
        authViewModel.login(
            usuario.editText?.text.toString().trim(),
            password.editText?.text.toString().trim()
        )
    }

    //BEFORE private fun guardarSesion(usuario: TextInputLayout, password: TextInputLayout, token: String)
    private fun guardarSesion(
        usuario: String,
        password: String,
        token: String,
        refreshToken: String,
    ) {
        var preferences: SharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE)
        var editor: SharedPreferences.Editor = preferences.edit()

        editor.putString("usuario", usuario)
        editor.putString("contrasenia", password)
        editor.putString("token", token)
//        editor.putString("tipo_cuenta", "ESTANDAR")
        editor.putString("refresh_token", refreshToken)

        editor.commit()

        irAHome()
    }

    private fun irAHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun irARegistro() {
        startActivity(Intent(this, RegistrationActivity::class.java))
    }
}