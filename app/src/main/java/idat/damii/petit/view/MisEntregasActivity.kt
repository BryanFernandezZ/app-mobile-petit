package idat.damii.petit.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import idat.damii.petit.common.adapter.MisEntregasAdapter
import idat.damii.petit.databinding.ActivityMisEntregasBinding
import idat.damii.petit.model.Ubicacion
import idat.damii.petit.viewmodel.UbicacionViewModel

class MisEntregasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMisEntregasBinding
    private lateinit var misEntregasAdapter: MisEntregasAdapter
    private lateinit var ubicacionViewModel: UbicacionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisEntregasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ubicacionViewModel = ViewModelProvider(this)
            .get(UbicacionViewModel::class.java)

        ubicacionViewModel.responseUbicaciones.observe(this, Observer { response ->
            misEntregasAdapter = MisEntregasAdapter(this, response)
            binding.recyclerMisEntregas.layoutManager = LinearLayoutManager(this)
            binding.recyclerMisEntregas.adapter = misEntregasAdapter
        })

        obtenerUbicaciones()

        binding.btnVolverMisEntregas.setOnClickListener {
            onBackPressed()
        }
    }

    private fun obtenerUbicaciones() {
        ubicacionViewModel.obtenerUbicaciones(2)
    }
}