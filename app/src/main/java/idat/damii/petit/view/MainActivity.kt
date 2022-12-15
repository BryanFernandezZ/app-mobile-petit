package idat.damii.petit.view

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.Profile
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import idat.damii.petit.common.values.App
import idat.damii.petit.R
import idat.damii.petit.common.util.AppPreferences
import idat.damii.petit.databinding.ActivityMainBinding
import idat.damii.petit.common.values.TipoCuenta
import idat.damii.petit.common.util.ImagenUtil
import idat.damii.petit.common.util.MensajeUtil
import idat.damii.petit.retrofit.response.LoginResponse
import idat.damii.petit.viewmodel.AuthViewModel
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import org.json.JSONObject

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var app: App
    private var emailFacebook: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = applicationContext as App

        authViewModel = ViewModelProvider(this)
            .get(AuthViewModel::class.java)

        cargarDatosDeUsuario()

        //Carrusel
        val list = mutableListOf<CarouselItem>()
        val carousel: ImageCarousel = binding.carousel2

        carousel.registerLifecycle(lifecycle = lifecycle)

        list.add(CarouselItem(R.drawable.nariz_de_perro))
        list.add(CarouselItem(R.drawable.gatito))
        list.add(CarouselItem(R.drawable.perro_blanco))

        carousel.addData(list)

        binding.civProfile.setOnClickListener(this)
        binding.cardCita.setOnClickListener(this)
        binding.cardMascota.setOnClickListener(this)
        binding.cardMisEntregas.setOnClickListener(this)

        authViewModel.responseDatosUsuario.observe(this, Observer { response ->
            app.usuario = response[0]

            if (app.urlFoto.isEmpty())
                binding.civProfile.setImageBitmap(
                    ImagenUtil.obtenerImagenDelTextoCodificado(
                        app.usuario.photo
                    )
                )
            else
                Glide.with(this)
                    .load(app.urlFoto)
                    .into(binding.civProfile)
        })
    }

    private fun refrescarToken(response: LoginResponse?) {
        val editor: SharedPreferences.Editor = AppPreferences.getSharedPreferences()?.edit()!!
        editor.remove("token")
        editor.commit()
        editor.putString("token", response?.access_token)
        editor.commit()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.civProfile -> irAMiPerfil()
            R.id.cardCita -> irARegistroDeCita()
            R.id.cardMascota -> irAVerMascotas()
            R.id.cardMisEntregas -> irAMisEntregas()
        }
    }

    private fun irAMisEntregas() {
        startActivity(Intent(this, MisEntregasActivity::class.java))
    }

    private fun irAMiPerfil() {
        startActivity(Intent(this, MiPerfilActivity::class.java))
    }

    private fun irAVerMascotas() {
        startActivity(Intent(this, MascotaActivity::class.java))
    }

    private fun cargarDatosDeUsuario() {
        val preferences: SharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE)
        var tipo: String? = preferences.getString("tipo_cuenta", "tipo")

        when (tipo) {
            TipoCuenta.ESTANDAR.name -> obtenerInformacionDelUsuarioBasico()
            TipoCuenta.GOOGLE.name -> obtenerInformacionDelUsuarioConGoogle()
            TipoCuenta.FACEBOOK.name -> obtenerInformacionDelUsuarioConFacebook()
        }
    }

    private fun irARegistroDeCita() {
        val intent = Intent(this, CitaActivity::class.java)
        startActivity(intent)
    }

    private fun obtenerInformacionDelUsuarioBasico() {
        val preferences: SharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE)
        val email: String = preferences.getString("usuario", "")!!

        if (email.contains("@petit.com")) {
            binding.cardMisEntregas.visibility = View.VISIBLE
            binding.cardCita.visibility = View.GONE
            binding.cardMascota.visibility = View.GONE
        }
        authViewModel.obtenerDatosUsuario(email)
    }

    private fun obtenerInformacionDelUsuarioConGoogle() {
        val acct: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)

        if (acct != null) {
            Log.d("Dato1", acct.displayName + "")
            Log.d("Dato2", acct.givenName + "")
            Log.d("Dato3", acct.familyName + "")
            Log.d("Dato4", acct.email + "")
            Log.d("Dato5", acct.id + "")

            Glide.with(this)
                .load(acct.photoUrl)
                .into(binding.civProfile)
        }
    }

    private fun obtenerInformacionDelUsuarioConFacebook() {
        obtenerOtrosDatosDeFacebook(AccessToken.getCurrentAccessToken())
        if (AccessToken.getCurrentAccessToken() == null) irALogin()
        else {
            var profile: Profile? = Profile.getCurrentProfile()

            if (profile != null) {
                Log.d("Dato1", profile.firstName + "") //Nombre
                Log.d("Dato2", profile.name + "")
                Log.d("Dato3", profile.lastName + "")  //Apellido
                Log.d("Dato5", profile.id + "")        //Posible password
            }
        }
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

                    var email = obj?.getString("email")  //Email
                    if (email != null) {
                        emailFacebook = email
                    }

                    val urlFoto: String = obj?.getJSONObject("picture")?.getJSONObject("data")
                        ?.getString("url")!! // Foto

                    Glide.with(applicationContext)
                        .load(urlFoto)
                        .into(binding.civProfile)
                }
            })

        val parameters = Bundle()
        parameters.putString("fields", "id,name,email,picture")
        graphRequest.parameters = parameters
        graphRequest.executeAsync()
    }

    private fun irALogin() {
        val app: App = applicationContext as App
        app.logout = true

        var intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}