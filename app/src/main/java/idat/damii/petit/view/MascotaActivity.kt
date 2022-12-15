package idat.damii.petit.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import idat.damii.petit.common.adapter.MascotaAdapter
import idat.damii.petit.databinding.ActivityMascotaBinding
import idat.damii.petit.viewmodel.MascotaViewModel

class MascotaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMascotaBinding
    private lateinit var mascotaViewModel: MascotaViewModel
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMascotaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mascotaViewModel = ViewModelProvider(context)
            .get(MascotaViewModel::class.java)

        mascotaViewModel.responseObtenerMascotas.observe(this, Observer { response ->
            val mascotaAdapter = MascotaAdapter(context, response)
            binding.recyclerMascotas.layoutManager = LinearLayoutManager(context)
            binding.recyclerMascotas.adapter = mascotaAdapter
        })

        obtenerMascotas()

        binding.btnVolverMascota.setOnClickListener {
            onBackPressed()
        }
    }

    private fun obtenerMascotas() {
        mascotaViewModel.obtenerMascotas()
    }
}