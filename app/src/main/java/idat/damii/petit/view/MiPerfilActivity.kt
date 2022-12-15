package idat.damii.petit.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import idat.damii.petit.R
import idat.damii.petit.common.adapter.MisCitasAdapter
import idat.damii.petit.common.util.ImagenUtil
import idat.damii.petit.common.values.App
import idat.damii.petit.common.values.TipoCuenta
import idat.damii.petit.databinding.ActivityMiPerfilBinding
import idat.damii.petit.retrofit.response.MiCitasResponse
import idat.damii.petit.viewmodel.CitaViewModel

class MiPerfilActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMiPerfilBinding
    private lateinit var citaViewModel: CitaViewModel
    private lateinit var alertDialogCerrarSesion: AlertDialog
    private lateinit var app: App
    private var idUsuario: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMiPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = applicationContext as App
        idUsuario = app.usuario.id

        crearCerrarSesionDialog()
        cargarDatosUsuario()

        citaViewModel = ViewModelProvider(this)
            .get(CitaViewModel::class.java)

        citaViewModel.responseObtenerMisCitas.observe(this, Observer { response ->
            cargarView(response)
        })

        binding.cardMisCitas.setOnClickListener(this)
        binding.cardActualizarMiUbicacion.setOnClickListener(this)
        binding.cardCerrarSesion.setOnClickListener(this)
        binding.btnVolverPerfil.setOnClickListener(this)
    }

    private fun crearCerrarSesionDialog() {
        alertDialogCerrarSesion =
            AlertDialog.Builder(this).setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de cerrar sesión?")
                .setPositiveButton("Si") { p1, p2 ->
                    cerrarSesion()
                }
                .setNegativeButton("No") { p1, p2 ->
                    alertDialogCerrarSesion.dismiss()
                }
                .create()
    }

    private fun cargarDatosUsuario() {
        if (app.urlFoto.isEmpty())
            binding.civFotoPerfil.setImageBitmap(ImagenUtil.obtenerImagenDelTextoCodificado(app.usuario.photo))
        else Glide.with(this)
            .load(app.urlFoto)
            .into(binding.civFotoPerfil)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.cardMisCitas -> idUsuario?.let { citaViewModel.obtenerMisCitas(it) }
            R.id.cardActualizarMiUbicacion -> irAMaps()
            R.id.cardCerrarSesion -> alertDialogCerrarSesion.show()
            R.id.btnVolverPerfil -> onBackPressed()
        }
    }

    private fun cerrarSesion() {
        binding.layoutProgressBarMiPerfil.visibility = View.VISIBLE
        binding.scvMiPerfil.visibility = View.GONE

        val preferences: SharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE)
        var tipo: String? = preferences.getString("tipo_cuenta", "tipo")

        when (tipo) {
            TipoCuenta.ESTANDAR.name -> cerrarSesionBasica()
            TipoCuenta.GOOGLE.name -> cerrarSesionGoogle()
            TipoCuenta.FACEBOOK.name -> cerrarSesionFacebook()
        }
    }

    private fun cerrarSesionBasica() {
        var preferences: SharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE)
        var editor: SharedPreferences.Editor = preferences.edit()

        editor.remove("usuario")
        editor.remove("contrasenia")
        editor.remove("tipo_cuenta")
        editor.remove("token")
        editor.remove("refresh_token")

        editor.commit()
        Log.e("Test: ", "Mensaje de CERRAR BASICO")
        irALogin()
    }

    private fun cerrarSesionGoogle() {
        var preferences: SharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE)
        var editor: SharedPreferences.Editor = preferences.edit()

        editor.remove("usuario")
        editor.remove("contrasenia")
        editor.remove("tipo_cuenta")
        editor.remove("token")
        editor.remove("refresh_token")
        editor.commit()

        irALogin()
    }

    private fun cerrarSesionFacebook() {
        LoginManager.getInstance().logOut()
        var preferences: SharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE)
        var editor: SharedPreferences.Editor = preferences.edit()

        editor.remove("usuario")
        editor.remove("contrasenia")
        editor.remove("tipo_cuenta")
        editor.remove("token")
        editor.remove("refresh_token")
        editor.commit()
        irALogin()
    }

    private fun irAMaps() {
        if (validarPermisoUbicacion())
            startActivity(Intent(this, MapsPerfilActivity::class.java))
        else
            solicitarPermisoUbicacion()
    }

    private fun irALogin() {
        val app: App = applicationContext as App
        app.logout = true

        var intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun mostrarMisCitas(view: View) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Mis Citas")
            .setView(view)
            .create()

        alertDialog.show()
    }

    private fun cargarView(response: List<MiCitasResponse>) {
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view: View = inflater.inflate(R.layout.miscitas_template, null, false)

        val recyclerMisCitas: RecyclerView = view.findViewById(R.id.recyclerMisCitas)

        val adapter = MisCitasAdapter(this, response)
        recyclerMisCitas.layoutManager = LinearLayoutManager(this)
        recyclerMisCitas.adapter = adapter

        mostrarMisCitas(view)
    }

    private fun solicitarPermisoUbicacion() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1)
    }

    private fun validarPermisoUbicacion(): Boolean {
        val fine_location = ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse_location = ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
        var permitido = false
        if (fine_location == PackageManager.PERMISSION_GRANTED && coarse_location == PackageManager.PERMISSION_GRANTED)
            permitido = true
        return permitido
    }
}