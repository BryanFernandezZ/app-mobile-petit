package idat.damii.petit.retrofit.response

data class LoginResponse(
    var access_token: String,
    var token_type: String,
    var refresh_token: String,
    var expires_in: Int,
    var scope: String,
    var jti: String
)
