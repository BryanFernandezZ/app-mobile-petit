package idat.damii.petit.retrofit.request

data class LoginRequest(
    var username: String,
    var password: String,
    var grant_type: String
)
