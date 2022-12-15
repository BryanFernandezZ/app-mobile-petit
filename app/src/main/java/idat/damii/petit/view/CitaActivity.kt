package idat.damii.petit.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import idat.damii.petit.common.values.App
import idat.damii.petit.R
import idat.damii.petit.databinding.ActivityCitaBinding
import idat.damii.petit.model.Cita
import idat.damii.petit.common.util.MensajeUtil
import idat.damii.petit.viewmodel.CitaViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class CitaActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityCitaBinding
    private lateinit var fechaAdapter: ArrayAdapter<String>
    private lateinit var horaAdapter: ArrayAdapter<String>
    private lateinit var fechas: ArrayList<String>
    private lateinit var horas: ArrayList<String>
    private lateinit var citaRegistro: Cita
    private var idTipoServicio: Int = 0
    private var fechaAtencion: String = ""
    private var horaAtencion: String = ""

    private lateinit var citaViewModel: CitaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        citaRegistro = Cita(null, null, null, null, null, null)

        fechaAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)
        horaAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerServicios.onItemSelectedListener = this
        binding.spinnerHoras.onItemSelectedListener = this
        binding.spinnerFechas.onItemSelectedListener = this

        citaViewModel = ViewModelProvider(this)
            .get(CitaViewModel::class.java)

        setearSpinners()

        citaViewModel.responseGetFechas.observe(this, Observer { response ->
            fechas = response as ArrayList<String>
            fechaAdapter.addAll(formatearFechas(fechas))
        })

        citaViewModel.responseRegistrarCitaFinal.observe(this, Observer { response ->
            MensajeUtil.enviarMensaje(binding.root, response.message)
        })

        binding.btnRegistrarCita.setOnClickListener {
            registrarCita()
        }

        binding.btnVolverCita.setOnClickListener {
            onBackPressed()
        }

        binding.spinnerFechas.adapter = fechaAdapter
        binding.spinnerHoras.adapter = horaAdapter
    }

    private fun setearSpinners() {
        cargarFechasAtencion()
//        cargarHorasAtencion()
        cargarHorasAtencion2()
        cargarServicios()
    }

    private fun cargarServicios() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.services_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.spinnerServicios.adapter = adapter
        }
    }

    private fun cargarFechasAtencion() {
        citaViewModel.getFechas(this)
    }

    private fun cargarHorasAtencion2() {
        horas = ArrayList()
        horas.add("09:00")

        for (i in 10..18) {
            if (i != 13) horas.add("${i}:00")
        }

        horaAdapter.addAll(horas)
    }

    private fun formatearFechas(lista: ArrayList<String>): ArrayList<String> {
        var output = ArrayList<String>()
        var format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        for (fecha: String in lista) {
            var date = LocalDate.parse(fecha, format)
            output.add("${date.dayOfMonth}-${date.monthValue}-${date.year}")
        }

        return output
    }

    private fun registrarCita() {
        citaRegistro.dateAttention = fechaAtencion + "T" + horaAtencion
        citaRegistro.dateIssued = LocalDateTime.now().toString()
        val app: App = applicationContext as App

        citaViewModel.registrarCitaFinal(idTipoServicio, app.usuario.id!!, 1, citaRegistro)
    }

    override fun onItemSelected(view: AdapterView<*>, p1: View?, pos: Int, p3: Long) {
        when (view.id) {
            R.id.spinnerFechas -> fechaAtencion =
                fechas[pos] //citaRegistro.dateAttention = fechas[pos] + "T00:00:00"
            R.id.spinnerHoras -> horaAtencion = horas[pos] + ":00"
            R.id.spinnerServicios -> {
                when (pos) {
                    0 -> {
                        idTipoServicio = 1
                        citaRegistro.price = 30.00
                    }
                    1 -> {
                        idTipoServicio = 2
                        citaRegistro.price = 50.00
                    }
                    2 -> {
                        idTipoServicio = 3
                        citaRegistro.price = 50.00
                    }
                }
            }
        }
    }

    override fun onNothingSelected(view: AdapterView<*>) {
        when (view.id) {
            R.id.spinnerFechas -> fechaAtencion = fechas[0]
            R.id.spinnerHoras -> horaAtencion = horas[0] + ":00"
            R.id.spinnerServicios -> {
                idTipoServicio = 1
                citaRegistro.price = 30.00
            }
        }
    }
}