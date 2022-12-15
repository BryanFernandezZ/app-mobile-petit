package idat.damii.petit.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import idat.damii.petit.R
import idat.damii.petit.common.util.AppPreferences
import idat.damii.petit.common.values.App
import idat.damii.petit.databinding.ActivityCuestionarioAdopcionBinding
import idat.damii.petit.model.CustionarioRespuesta
import idat.damii.petit.model.Mascota
import idat.damii.petit.retrofit.request.Adopcion
import idat.damii.petit.retrofit.request.CorreoRequest
import idat.damii.petit.retrofit.response.AccountUserResponse
import idat.damii.petit.retrofit.response.LocationResponse
import idat.damii.petit.viewmodel.AdopcionViewModel
import idat.damii.petit.viewmodel.CorreoViewModel
import idat.damii.petit.viewmodel.LocationViewModel
import idat.damii.petit.viewmodel.MascotaViewModel

class CuestionarioAdopcionActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityCuestionarioAdopcionBinding
    private lateinit var correoViewModel: CorreoViewModel
    private lateinit var mascotaViewModel: MascotaViewModel
    private lateinit var adopcionViewModel: AdopcionViewModel
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var correo: CorreoRequest
    private lateinit var app: App
    private lateinit var mascota: Mascota
    private lateinit var alertDialog: AlertDialog
    private lateinit var alertDialogConfirmacion: AlertDialog
    private lateinit var alertUbicacion: AlertDialog
    private var custionarioRespuesta: CustionarioRespuesta = CustionarioRespuesta(null, null, null)
    private var correoIsSuccessFull: Boolean = false
    private var adopcionIsSuccessFull: Boolean = false
    private var mascotaId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCuestionarioAdopcionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = applicationContext as App

        crearDialog()

        //ViewModel
        correoViewModel = ViewModelProvider(this)
            .get(CorreoViewModel::class.java)
        mascotaViewModel = ViewModelProvider(this)
            .get(MascotaViewModel::class.java)
        adopcionViewModel = ViewModelProvider(this)
            .get(AdopcionViewModel::class.java)
        locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]

        obtenerMascotaSeleccionada()

        correoViewModel.enviarCorreoResponse.observe(this, Observer { response ->
            correoIsSuccessFull = true

            if (correoIsSuccessFull && adopcionIsSuccessFull) {
                alertDialog.dismiss()
                Log.d("Exito Custionario", response.message)
                alertDialogConfirmacion.show()
            }
        })

        adopcionViewModel.responseGuardarSolicitudAdopcion.observe(this, Observer { response ->
            adopcionIsSuccessFull = true

            if (correoIsSuccessFull && adopcionIsSuccessFull) {
                alertDialog.dismiss()
                Log.d("Exito Custionario", response.toString())
                alertDialogConfirmacion.show()
            }
        })

        mascota = Mascota(null, "", "", "", "", "")

        //MascotaViewModel
        mascotaViewModel.responseGetMascotas.observe(this, Observer { response ->
            mascota.name = response.name
        })


        //LocationViewModel
        locationViewModel.responseObtenerUbicacion.observe(this, Observer { response ->
            validarUbicacion(response)
        })

        binding.rbtnUnoSi.setOnClickListener(this)
        binding.rbtnUnoNo.setOnClickListener(this)
        binding.rbtnTresSi.setOnClickListener(this)
        binding.rbtnTresNo.setOnClickListener(this)
        binding.btnFormularioCotinuar.setOnClickListener(this)
    }

    private fun crearDialog() {
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view: View = inflater.inflate(R.layout.loading_snoopy_template, null, false)
        var txvEspere: TextView = view.findViewById(R.id.txvEspere)
        txvEspere.text = "Enviando Respuestas\nEspere por favor..."

        alertDialog = AlertDialog.Builder(this)
            .setView(view)
            .setOnCancelListener {
                alertDialog.show()
            }
            .create()

        alertDialogConfirmacion = AlertDialog.Builder(this)
            .setTitle("¡Bien Hecho!")
            .setMessage("Te enviaremos un correo electronico con la confirmacion.\nMuchas Gracias <3")
            .setPositiveButton("Ok") { dialogInterface, p1 ->
                alertDialogConfirmacion.dismiss()
                startActivity(Intent(this, MascotaActivity::class.java))
            }
            .create()

        alertUbicacion = AlertDialog.Builder(this)
            .setTitle("Ubicacion no configurada")
            .setMessage("Necesitas configurar una ubicacion primero'\nPresiona OK para configurarla")
            .setPositiveButton("Ok") { p0, p1 ->
                startActivity(Intent(this, MiPerfilActivity::class.java))
                finish()
            }
            .setNegativeButton("Cancelar") { di, p0 ->
                alertUbicacion.dismiss()
            }.create()
    }

    private fun obtenerMascotaSeleccionada() {
        val bundle: Bundle? = intent.extras
        val id: Int = bundle?.getInt("id")!! //ID DE LA MASCOTA PARA CONSUMIR
        mascotaId = id
        mascotaViewModel.getMascota(id)
    }

    private fun enviarRespuestas() {
        alertDialog.show()

        //Enviar correo
        custionarioRespuesta.preguntaDos = binding.txvCargo.editText?.text.toString().trim()
        var body = crearMensaje()
        correo = CorreoRequest("a20201535@idat.edu.pe", "Cuestionario PreAdopcion", body)
        correoViewModel.enviarCorreo(correo)

        //Enviar solicitud
        adopcionViewModel.guardarSolicitudAdopcion(app.usuario.id!!,
            1,
            mascotaId,
            Adopcion(null, body))
    }

    private fun crearMensaje(): String {
        val preferences = AppPreferences.getSharedPreferences()
        var body = "El cliente con correo ${
            preferences?.getString("usuario",
                "")
        } ha solicitado la adopcion de la mascota ${mascota.name}\n" +
                "Respuestas al cuestionario:\n" +
                "${resources.getString(R.string.formulario_pregunta_1)} -> ${custionarioRespuesta.preguntaUno}\n" +
                "${resources.getString(R.string.formulario_pregunta_2)} -> ${custionarioRespuesta.preguntaDos}\n" +
                "${resources.getString(R.string.formulario_pregunta_3)} -> ${custionarioRespuesta.preguntaTres}\n"

        return body
    }

    private fun ocultarPregunta2() {
        binding.txvCargo.editText?.text = null
        binding.layoutCargo.visibility = View.GONE
    }

    private fun mostrarPregunta2() {
        binding.layoutCargo.visibility = View.VISIBLE
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.rbtnUnoSi -> {
                custionarioRespuesta.preguntaUno = "Si"
                mostrarPregunta2()
            }
            R.id.rbtnUnoNo -> {
                custionarioRespuesta.preguntaUno = "No"
                ocultarPregunta2()
            }
            R.id.rbtnTresSi ->
                custionarioRespuesta.preguntaTres = "Si"

            R.id.rbtnTresNo ->
                custionarioRespuesta.preguntaTres = "No"

            R.id.btnFormularioCotinuar -> locationViewModel.obtenerUbicacion(app.usuario.id!!)
            R.id.btnVolverCustionario -> onBackPressed()
        }
    }

    private fun validarUbicacion(response: LocationResponse) {
        if (response.body != null) enviarRespuestas()
        else alertUbicacion.show()
    }
}