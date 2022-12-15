package idat.damii.petit.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import idat.damii.petit.common.values.Constantes
import idat.damii.petit.R
import idat.damii.petit.databinding.ActivityRegistrationBinding
import idat.damii.petit.common.util.MensajeUtil

class RegistrationActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegisterContinue.setOnClickListener(this)

        validarConRegex()
    }

    private fun validarConRegex() {
        val nombre = binding.registerName
        val apellido = binding.registerApellido
        val telefono = binding.registerPhone
        val dni = binding.registerDni

        nombre.editText?.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                if (nombre.editText?.text.toString().trim().length < 5) {
                    nombre.error =
                        "*Este campo debe contener como minimo cinco caracteres*"
                } else {
                    if (!Constantes.NAMES_PATTERN.matcher(nombre.editText?.text.toString().trim())
                            .matches()
                    ) nombre.error =
                        "*Este campo debe contener solo letras*"
                    else nombre.error = null
                }
            }
        }

        apellido.editText?.onFocusChangeListener =
            View.OnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    if (apellido.editText?.text.toString().trim().length < 10) {
                        apellido.error =
                            "*Este campo debe contener como minimo diez caracteres*"
                    } else {
                        if (!Constantes.NAMES_PATTERN.matcher(
                                apellido.editText?.text.toString().trim()
                            ).matches()
                        ) apellido.error =
                            "*Este campo debe contener solo letras*"
                        else apellido.error = null
                    }
                }
            }

        telefono.editText?.onFocusChangeListener =
            View.OnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    if (telefono.editText?.text.toString()
                            .trim().length != 9
                    ) {
                        telefono.error =
                            "*Este campo debe contener nueve digitos*"
                    } else {
                        if (!Constantes.PHONE_PATTERN.matcher(
                                telefono.editText?.text.toString().trim()
                            ).matches()
                        ) telefono.error =
                            "*Este campo debe contener solo digitos*"
                        else telefono.error = null
                    }
                }
            }

        dni.editText?.onFocusChangeListener =
            View.OnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    if (dni.editText?.text.toString()
                            .trim().length != 8
                    ) {
                        dni.error =
                            "*Este campo debe contener ocho digitos*"
                    } else {
                        dni.error = null
                        if (!Constantes.PHONE_PATTERN.matcher(
                                dni.editText?.text.toString().trim()
                            ).matches()
                        ) dni.error =
                            "*Este campo debe contener solo digitos*"
                        else dni.error = null
                    }
                }
            }

        dni.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.w("Before change", "Before change")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (dni.editText?.text.toString()
                        .trim().length != 8
                ) {
                    dni.error =
                        "*Este campo debe contener ocho digitos*"
                } else {
                    dni.error = null
                    if (!Constantes.PHONE_PATTERN.matcher(
                            dni.editText?.text.toString().trim()
                        ).matches()
                    ) dni.error =
                        "*Este campo debe contener solo digitos*"
                    else dni.error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.w("After change", "After change")
            }
        })
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnRegisterContinue -> continuarRegistro()
        }
    }

    private fun continuarRegistro() {
        if (validarFormulario()) {
            if (!Constantes.NAMES_PATTERN.matcher(
                    binding.registerName.editText?.text.toString().trim()
                ).matches() ||
                !Constantes.NAMES_PATTERN.matcher(
                    binding.registerApellido.editText?.text.toString().trim()
                ).matches() ||
                !Constantes.PHONE_PATTERN.matcher(
                    binding.registerPhone.editText?.text.toString().trim()
                ).matches() ||
                !Constantes.PHONE_PATTERN.matcher(
                    binding.registerDni.editText?.text.toString().trim()
                ).matches()
            ) {
                MensajeUtil.enviarMensaje(
                    binding.root,
                    "Alguno de los datos proporcionados es invalido"
                )
            } else irARegistroFinal()
        }
    }

    private fun validarFormulario(): Boolean {
        var salida = false

        val nombre = binding.registerName
        val apellido = binding.registerApellido
        val telefono = binding.registerPhone
        val dni = binding.registerDni

        if (nombre.editText?.text.toString().trim() != "" && apellido.editText?.text.toString()
                .trim() != "" && telefono.editText?.text.toString()
                .trim() != "" && dni.editText?.text.toString().trim() != ""
        ) {
            nombre.error = null
            apellido.error = null
            telefono.error = null
            dni.error = null
            salida = true
        } else {
            if (nombre.editText?.text.toString().trim() == "" && apellido.editText?.text.toString()
                    .trim() == "" && telefono.editText?.text.toString()
                    .trim() == "" && dni.editText?.text.toString().trim() == ""
            ) {
                nombre.error = "*Este campo es obligatorio*"
                apellido.error = "*Este campo es obligatorio*"
                telefono.error = "*Este campo es obligatorio*"
                dni.error = "*Este campo es obligatorio*"
                MensajeUtil.enviarMensaje(binding.root, "Todos los campos son obligatorios")
            } else if (nombre.editText?.text.toString()
                    .trim() != "" && apellido.editText?.text.toString()
                    .trim() != "" && telefono.editText?.text.toString().trim() != ""
            ) {
                nombre.error = null
                apellido.error = null
                telefono.error = null
                dni.error = "*Este campo es obligatorio*"
            } else if (apellido.editText?.text.toString()
                    .trim() != "" && telefono.editText?.text.toString()
                    .trim() != "" && dni.editText?.text.toString().trim() != ""
            ) {
                nombre.error = "*Este campo es obligatorio*"
                apellido.error = null
                telefono.error = null
                dni.error = null
            } else if (telefono.editText?.text.toString()
                    .trim() != "" && dni.editText?.text.toString()
                    .trim() != "" && nombre.editText?.text.toString().trim() != ""
            ) {
                nombre.error = null
                apellido.error = "*Este campo es obligatorio*"
                telefono.error = null
                dni.error = null
            } else if (dni.editText?.text.toString()
                    .trim() != "" && nombre.editText?.text.toString()
                    .trim() != "" && apellido.editText?.text.toString().trim() != ""
            ) {
                nombre.error = null
                apellido.error = null
                telefono.error = "*Este campo es obligatorio*"
                dni.error = null
            } else if (nombre.editText?.text.toString()
                    .trim() != "" && apellido.editText?.text.toString().trim() != ""
            ) {
                nombre.error = null
                apellido.error = null
                telefono.error = "*Este campo es obligatorio*"
                dni.error = "*Este campo es obligatorio*"
            } else if (apellido.editText?.text.toString()
                    .trim() != "" && telefono.editText?.text.toString().trim() != ""
            ) {
                nombre.error = "*Este campo es obligatorio*"
                apellido.error = null
                telefono.error = null
                dni.error = "*Este campo es obligatorio*"
            } else if (telefono.editText?.text.toString()
                    .trim() != "" && dni.editText?.text.toString().trim() != ""
            ) {
                nombre.error = "*Este campo es obligatorio*"
                apellido.error = "*Este campo es obligatorio*"
                telefono.error = null
                dni.error = null
            } else if (dni.editText?.text.toString()
                    .trim() != "" && nombre.editText?.text.toString().trim() != ""
            ) {
                nombre.error = null
                apellido.error = "*Este campo es obligatorio*"
                telefono.error = "*Este campo es obligatorio*"
                dni.error = null
            } else if (nombre.editText?.text.toString().trim() != "") {
                nombre.error = null
                apellido.error = "*Este campo es obligatorio*"
                telefono.error = "*Este campo es obligatorio*"
                dni.error = "*Este campo es obligatorio*"
            } else if (apellido.editText?.text.toString().trim() != "") {
                nombre.error = "*Este campo es obligatorio*"
                apellido.error = null
                telefono.error = "*Este campo es obligatorio*"
                dni.error = "*Este campo es obligatorio*"
            } else if (telefono.editText?.text.toString().trim() != "") {
                nombre.error = "*Este campo es obligatorio*"
                apellido.error = "*Este campo es obligatorio*"
                telefono.error = null
                dni.error = "*Este campo es obligatorio*"
            } else if (dni.editText?.text.toString().trim() != "") {
                nombre.error = "*Este campo es obligatorio*"
                apellido.error = "*Este campo es obligatorio*"
                telefono.error = "*Este campo es obligatorio*"
                dni.error = null
            }
        }

        return salida
    }

    private fun irARegistroFinal() {
        var intent = Intent(this, RegistrationFinalActivity::class.java)
            .apply {
                putExtra("nombre", binding.registerName.editText?.text.toString().trim())
                putExtra("apellido", binding.registerApellido.editText?.text.toString().trim())
                putExtra("telefono", binding.registerPhone.editText?.text.toString().trim())
                putExtra("dni", binding.registerDni.editText?.text.toString().trim())
            }

        startActivity(intent)
    }
}