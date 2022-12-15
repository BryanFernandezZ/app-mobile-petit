package idat.damii.petit.common.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

object AppPreferences {
    private var sharedPreferences: SharedPreferences? = null

    fun onCreate(context: Context){
        sharedPreferences = context.getSharedPreferences("sesion", AppCompatActivity.MODE_PRIVATE)
    }

    fun getSharedPreferences(): SharedPreferences? {
        return sharedPreferences
    }
}