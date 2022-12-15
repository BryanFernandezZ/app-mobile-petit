package idat.damii.petit.repository.network

import idat.damii.petit.common.values.Constantes
import idat.damii.petit.retrofit.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object PetitClient {
    private var okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(ApiInterceptor())
        .build()

    private fun getRetrofit() = Retrofit.Builder()
        .baseUrl(Constantes.API_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val usuarioService: UsuarioAPI by lazy {
        getRetrofit().create(UsuarioAPI::class.java)
    }

    val cuentaService: CuentaAPI by lazy {
        getRetrofit().create(CuentaAPI::class.java)
    }

    val citaService: CitaAPI by lazy {
        getRetrofit().create(CitaAPI::class.java)
    }

    val mascotaService: MascotaAPI by lazy {
        getRetrofit().create(MascotaAPI::class.java)
    }

    val correoService: CorreoAPI by lazy {
        getRetrofit().create(CorreoAPI::class.java)
    }

    val locationService: LocationAPI by lazy {
        getRetrofit().create(LocationAPI::class.java)
    }

    val adopcionService: AdopcionAPI by lazy {
        getRetrofit().create(AdopcionAPI::class.java)
    }

    val ubicacionService: UbicacionAPI by lazy {
        getRetrofit().create(UbicacionAPI::class.java)
    }
}