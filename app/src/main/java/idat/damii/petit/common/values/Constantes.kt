package idat.damii.petit.common.values

import java.util.regex.Pattern

class Constantes {
    companion object {
        const val API_URL: String = "http://192.168.18.119:8080/petit/"

        //Authorization
        const val serverClientId = "oscar"
        const val serverClientSecret = "12345"
        const val payloadGrantType = "password"

        val EMAIL_PATTERN: Pattern = Pattern.compile("[A-Za-z0-9+_.-]+@[a-z]+.(com)")
        val PASSWORD_PATTERN: Pattern =
            Pattern.compile("(?=(.*[0-9]))(?=.*[\\!@#$%^&*()\\\\{}\\-_+=~`|:;\"'<>,./?])(?=.*[a-z])(?=(.*[A-Z]))(?=(.*)).{8,}")
        val NAMES_PATTERN: Pattern = Pattern.compile("[A-Za-z ]+")
        val PHONE_PATTERN: Pattern =
            Pattern.compile("[0-9]+")
    }
}