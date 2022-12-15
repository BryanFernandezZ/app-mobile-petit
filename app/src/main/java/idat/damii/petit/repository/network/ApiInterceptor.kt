package idat.damii.petit.repository.network

import android.content.SharedPreferences
import idat.damii.petit.common.util.AppPreferences
import idat.damii.petit.common.values.App
import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token: String? = AppPreferences.getSharedPreferences()?.getString("token", "")
        val request = chain.request()

        if (token == null || token == "") return chain.proceed(request)

        val newRequest =
            chain.request().newBuilder().addHeader("Authorization", "Bearer $token").build()

        return chain.proceed(newRequest)
    }

}