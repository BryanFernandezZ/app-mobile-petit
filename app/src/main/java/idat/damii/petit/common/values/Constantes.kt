package idat.damii.petit.common.values

import android.content.res.Resources
import idat.damii.petit.R
import java.util.regex.Pattern

class Constantes {
    companion object {
        val API_URL: String = Resources.getSystem().getString(R.string.api_url)

        //Authorization
        val serverClientId = Resources.getSystem().getString(R.string.server_client_Id)
        val serverClientSecret = Resources.getSystem().getString(R.string.server_secret)
        val payloadGrantType = Resources.getSystem().getString(R.string.payload_grant_type)

        val EMAIL_PATTERN: Pattern = Pattern.compile("[A-Za-z0-9+_.-]+@[a-z]+.(com)")
        val PASSWORD_PATTERN: Pattern =
            Pattern.compile("(?=(.*[0-9]))(?=.*[\\!@#$%^&*()\\\\{}\\-_+=~`|:;\"'<>,./?])(?=.*[a-z])(?=(.*[A-Z]))(?=(.*)).{8,}")
        val NAMES_PATTERN: Pattern = Pattern.compile("[A-Za-z ]+")
        val PHONE_PATTERN: Pattern =
            Pattern.compile("[0-9]+")
    }
}