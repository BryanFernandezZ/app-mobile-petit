package idat.damii.petit.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import idat.damii.petit.common.values.Constantes
import androidx.lifecycle.Observer
import idat.damii.petit.R
import idat.damii.petit.databinding.ActivityRegistrationFinalBinding
import idat.damii.petit.model.Usuario
import idat.damii.petit.common.util.ImagenUtil
import idat.damii.petit.common.util.MensajeUtil
import idat.damii.petit.retrofit.request.AccountUserRequest
import idat.damii.petit.viewmodel.AuthViewModel
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.math.min
import java.text.SimpleDateFormat

class RegistrationFinalActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRegistrationFinalBinding
    private lateinit var usuarioRegistro: Usuario
    private lateinit var alertDialog: AlertDialog
    private lateinit var authViewModel: AuthViewModel

    private var rutaFotoActual = ""
    private var isProfileImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationFinalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarDatosDeUsuario()

        binding.btnRegisterFinal.setOnClickListener(this)
        binding.civFotoRegistro.setOnClickListener(this)

        validarConRegex()

        alertDialog = AlertDialog.Builder(this)
            .setTitle("Guardando")
            .setMessage("Espere por favor...").create()

        authViewModel = ViewModelProvider(this)
            .get(AuthViewModel::class.java)

        authViewModel.responseCuentaUsuarioRegistro.observe(this, Observer { response ->
            if (response.status == 201) {
                alertDialog.dismiss()
                MensajeUtil.enviarMensaje(binding.root, response.message)
                irALogin()
            } else {
                alertDialog.dismiss()
                MensajeUtil.enviarMensaje(binding.root, response.message)
            }
        })
    }

    private fun validarConRegex() {
        val correo = binding.registerEmail
        val contrasenia = binding.registerPassword
        val confirmarContrasenia = binding.registerConfirmPassword

        correo.editText?.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                if (correo.editText?.text.toString().trim().length < 12) {
                    correo.error =
                        "*Este campo debe contener como minimo doce caracteres*"
                } else {
                    if (!Constantes.EMAIL_PATTERN.matcher(correo.editText?.text.toString().trim())
                            .matches()
                    ) correo.error =
                        "Dato invalido\nEjemplo: correo@example.com"
                    else correo.error = null
                }
            }
        }

        contrasenia.editText?.onFocusChangeListener =
            View.OnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    if (contrasenia.editText?.text.toString().trim().length < 8) {
                        contrasenia.error =
                            "*Este campo debe contener como minimo ocho caracteres*"
                    } else {
                        if (!Constantes.PASSWORD_PATTERN.matcher(
                                contrasenia.editText?.text.toString().trim()
                            ).matches()
                        ) contrasenia.error =
                            "*Dato invalido*\nDebe contener por lo menos una mayuscula, una minuscula, un numero y un caracter especial"
                        else contrasenia.error = null
                    }
                }
            }

        confirmarContrasenia.editText?.onFocusChangeListener =
            View.OnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    if (confirmarContrasenia.editText?.text.toString()
                            .trim().length < 8
                    ) {
                        confirmarContrasenia.error =
                            "*Este campo debe contener como minimo ocho caracteres*"
                    } else {
                        if (!Constantes.PASSWORD_PATTERN.matcher(
                                confirmarContrasenia.editText?.text.toString().trim()
                            ).matches()
                        ) confirmarContrasenia.error =
                            "*Dato invalido*\nDebe contener por lo menos una mayuscula, una minuscula, un numero y un caracter especial"
                        else confirmarContrasenia.error = null
                    }
                }
            }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnRegisterFinal -> validarDatos()
            R.id.civFotoRegistro -> mostrarOpciones()
        }
    }

    override fun onStart() {
        super.onStart()
        isProfileImage = false
    }

    private fun validarDatos() {
        if (validarFormulario()) {
            if (!Constantes.EMAIL_PATTERN.matcher(
                    binding.registerEmail.editText?.text.toString().trim()
                ).matches() ||
                !Constantes.PASSWORD_PATTERN.matcher(
                    binding.registerPassword.editText?.text.toString().trim()
                ).matches() ||
                !Constantes.PASSWORD_PATTERN.matcher(
                    binding.registerConfirmPassword.editText?.text.toString().trim()
                ).matches()
            ) {
                MensajeUtil.enviarMensaje(
                    binding.root,
                    "Alguno de los datos proporcionados es invalido"
                )
            } else registrarUsuario()
        }
    }

    private fun registrarUsuario() {
        alertDialog.show()

        usuarioRegistro.photo =
            if (isProfileImage) ImagenUtil.obtenerFotoCodificada(binding.civFotoRegistro.drawable as BitmapDrawable)
            else ImagenUtil.obtenerFotoCodificada(ContextCompat.getDrawable(this,
                R.drawable.default_profile_picture) as BitmapDrawable)

        authViewModel.registrarCuentaUser(
            AccountUserRequest(
                null,
                usuarioRegistro.names,
                usuarioRegistro.lastNames,
                usuarioRegistro.dni,
                usuarioRegistro.phone,
                usuarioRegistro.photo,
                null,
                binding.registerEmail.editText?.text.toString().trim(),
                binding.registerPassword.editText?.text.toString().trim(),
                1
            )
        )
    }

    private fun irALogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun mostrarOpciones() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        builder.setTitle("Elige una opcion")
            .setItems(
                R.array.opciones_foto_array
            ) { _, pos ->
                when (pos) {
                    0 -> tomarFoto()
                    1 -> cargarFoto()
                }
            }.show()
    }

    private fun permisoEscrituraAlmacenamiento(): Boolean {
        val resultado = ContextCompat.checkSelfPermission(
            applicationContext,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        var exito = false

        if (resultado == PackageManager.PERMISSION_GRANTED) exito = true
        return exito
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) intentTomarFoto()
            else MensajeUtil.enviarMensaje(binding.root, "Permiso denegado")
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private val getTakePhotorResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                isProfileImage = true
                val dataIntent: Intent? = it.data
                val path: Uri? = dataIntent?.data
                binding.civFotoRegistro.setImageURI(path)
            }
        }

    private fun cargarFoto() {
        val cargarFotoIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        getTakePhotorResult.launch(cargarFotoIntent)
    }

    private fun tomarFoto() {
        if (permisoEscrituraAlmacenamiento()) {
            try {
                intentTomarFoto()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else solicitarPermiso()
    }

    private fun solicitarPermiso() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            123
        )
    }

    private fun intentTomarFoto() {
        val tomarFotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (tomarFotoIntent.resolveActivity(this.packageManager) != null) {
            val archivoFoto = crearArchivoTemporal()

            if (archivoFoto != null) {
                val photoURI = obtenerContentURI(archivoFoto)
                tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                getResult.launch(tomarFotoIntent)
            }
        }
    }

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                isProfileImage = true
                mostrarFoto()
            }
        }

    private fun mostrarFoto() {
        val anchoIv: Int = binding.civFotoRegistro.width
        val altoIv: Int = binding.civFotoRegistro.height
        val bmOptions = BitmapFactory.Options()

        bmOptions.inJustDecodeBounds = true

        BitmapFactory.decodeFile(rutaFotoActual, bmOptions)

        val anchoFoto = bmOptions.outWidth
        val altoFoto = bmOptions.outHeight
        val escalaImagen = min(anchoFoto / anchoIv, altoFoto / altoIv)

        bmOptions.inSampleSize = escalaImagen
        bmOptions.inJustDecodeBounds = false

        val bitmap = BitmapFactory.decodeFile(rutaFotoActual, bmOptions)
        binding.civFotoRegistro.setImageBitmap(bitmap)
    }

    private fun obtenerContentURI(archivoFoto: File): Uri {
        return if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) FileProvider.getUriForFile(
            applicationContext, "idat.damii.petit.fileprovider", archivoFoto
        )
        else Uri.fromFile(archivoFoto)
    }

    private fun crearArchivoTemporal(): File {
        val nombreImagen = "JPEG_" + SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val directorioImagenes: File = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val archivoTemporal: File = File.createTempFile(nombreImagen, ".jpg", directorioImagenes)
        rutaFotoActual = archivoTemporal.absolutePath
        return archivoTemporal
    }

    private fun validarFormulario(): Boolean {
        var salida = false

        val correo = binding.registerEmail
        val contrasenia = binding.registerPassword
        val confirmarContrasenia = binding.registerConfirmPassword

        if (correo.editText?.text.toString().trim() != "" && contrasenia.editText?.text.toString()
                .trim() != "" && confirmarContrasenia.editText?.text.toString().trim() != ""
        ) {
            correo.error = null
            contrasenia.error = null
            confirmarContrasenia.error = null
            salida = true
        } else {
            if (correo.editText?.text.toString()
                    .trim() == "" && contrasenia.editText?.text.toString()
                    .trim() == "" && confirmarContrasenia.editText?.text.toString().trim() == ""
            ) {
                correo.error = "*Este campo es obligatorio*"
                contrasenia.error = "*Este campo es obligatorio*"
                confirmarContrasenia.error = "*Este campo es obligatorio*"
                MensajeUtil.enviarMensaje(binding.root, "Todos los campos son obligatorios")
            } else if (correo.editText?.text.toString()
                    .trim() != "" && contrasenia.editText?.text.toString().trim() != ""
            ) {
                correo.error = null
                contrasenia.error = null
                confirmarContrasenia.error = "*Este campo es obligatorio*"
            } else if (contrasenia.editText?.text.toString()
                    .trim() != "" && confirmarContrasenia.editText?.text.toString().trim() != ""
            ) {
                correo.error = "*Este campo es obligatorio*"
                contrasenia.error = null
                confirmarContrasenia.error = null
            } else if (confirmarContrasenia.editText?.text.toString()
                    .trim() != "" && correo.editText?.text.toString().trim() != ""
            ) {
                correo.error = null
                contrasenia.error = "*Este campo es obligatorio*"
                confirmarContrasenia.error = null
            } else if (correo.editText?.text.toString().trim() != "") {
                Toast.makeText(applicationContext, "TEST1", Toast.LENGTH_SHORT).show()
                correo.error = null
                contrasenia.error = "*Este campo es obligatorio*"
                confirmarContrasenia.error = "*Este campo es obligatorio*"
            } else if (contrasenia.editText?.text.toString().trim() != "") {
                Toast.makeText(applicationContext, "TEST2", Toast.LENGTH_SHORT).show()
                correo.error = "*Este campo es obligatorio*"
                contrasenia.error = null
                confirmarContrasenia.error = "*Este campo es obligatorio*"
            } else if (confirmarContrasenia.editText?.text.toString().trim() != "") {
                correo.error = "*Este campo es obligatorio*"
                contrasenia.error = "*Este campo es obligatorio*"
                confirmarContrasenia.error = null
            }
        }

        return salida
    }

    private fun cargarDatosDeUsuario() {
        val bundle: Bundle? = intent.extras

        val nombreUsuario = bundle?.getString("nombre")
        val apellidoUsuario = bundle?.getString("apellido")
        val telefonoUsuario = bundle?.getString("telefono")
        val dniUsuario = bundle?.getString("dni")

        usuarioRegistro = Usuario(
            null,
            nombreUsuario!!,
            apellidoUsuario!!,
            dniUsuario!!,
            telefonoUsuario!!,
            ""
        )

        Log.w("Nombre: ", usuarioRegistro.names)
        Log.w("Apellido: ", usuarioRegistro.lastNames)
        Log.w("Telefono: ", usuarioRegistro.phone)
        Log.w("DNI: ", usuarioRegistro.dni)
    }
}