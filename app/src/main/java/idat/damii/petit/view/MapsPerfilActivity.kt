package idat.damii.petit.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import idat.damii.petit.R
import idat.damii.petit.common.util.MensajeUtil
import idat.damii.petit.common.values.App
import idat.damii.petit.databinding.ActivityMapsPerfilBinding
import idat.damii.petit.retrofit.request.LocationRequest
import idat.damii.petit.viewmodel.LocationViewModel

class MapsPerfilActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsPerfilBinding
    private lateinit var alertDialog: AlertDialog
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var ubicacion: LocationRequest
    private lateinit var app: App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapPerfil) as SupportMapFragment
        mapFragment.getMapAsync(this)

        app = applicationContext as App

        locationViewModel = ViewModelProvider(this)
            .get(LocationViewModel::class.java)

        alertDialog = AlertDialog.Builder(this)
            .setTitle("¿Quieres guardar esta ubicación?")
            .setPositiveButton("Si") { dialogInterface, p1 ->
                MensajeUtil.enviarMensaje(binding.root, "Click")
                guardarUbicacion(mMap)
            }
            .setNegativeButton("No") { dialogInterace, p1 ->
                cerrarDialog()
            }
            .create()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        locationViewModel.obtenerUbicacion(app.usuario.id!!)

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = false

        binding.imvMarcador.setOnClickListener {
            alertDialog.show()
        }

        binding.btnVolver.setOnClickListener {
            onBackPressed()
        }

        locationViewModel.responseGuardarUbicacion.observe(this, Observer { response ->
            Thread.sleep(1000)
            startActivity(Intent(this, MiPerfilActivity::class.java))
            finish()
        })

        locationViewModel.responseObtenerUbicacion.observe(this, Observer { response ->
            if (response.body != null) {
                ubicacion = LocationRequest(response.body?.id, "", "")
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(response.body?.latitude?.toDouble()!!,
                    response.body?.length?.toDouble()!!), 14f))
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(obtenerUbicacionActual(), 14f))
                LocationRequest(null, "", "")
            }
        })
    }

    private fun cerrarDialog() {
        alertDialog.dismiss()
    }

    private fun guardarUbicacion(mMap: GoogleMap) {

        val latitud = mMap.cameraPosition.target.latitude
        val longitud = mMap.cameraPosition.target.longitude

        ubicacion.latitude = latitud.toString()
        ubicacion.length = longitud.toString()

        locationViewModel.guardarUbicacion(app.usuario.id!!, ubicacion)
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacionActual(): LatLng {
        val locManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        var localization: Location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
        val myPos = LatLng(localization.latitude, localization.longitude)

        return myPos
    }
}