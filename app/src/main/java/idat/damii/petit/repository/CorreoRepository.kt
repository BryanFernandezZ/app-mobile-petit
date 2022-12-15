package idat.damii.petit.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import idat.damii.petit.repository.network.PetitClient
import idat.damii.petit.retrofit.request.CorreoRequest
import idat.damii.petit.retrofit.response.AccountUserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CorreoRepository {
    var enviarCorreoResponse = MutableLiveData<AccountUserResponse>()

    fun enviarCorreo(correo: CorreoRequest): MutableLiveData<AccountUserResponse> {

        val call: Call<AccountUserResponse> = PetitClient.correoService.enviarCorreo(correo)
        call.enqueue(object : Callback<AccountUserResponse> {
            override fun onResponse(
                call: Call<AccountUserResponse>,
                response: Response<AccountUserResponse>,
            ) {
                if (response.isSuccessful && response.body() != null)
                    enviarCorreoResponse.value = response.body()
            }

            override fun onFailure(call: Call<AccountUserResponse>, t: Throwable) {
                Log.e("Error al enviar formulario", t.message.toString())
            }

        })

        return enviarCorreoResponse
    }
}