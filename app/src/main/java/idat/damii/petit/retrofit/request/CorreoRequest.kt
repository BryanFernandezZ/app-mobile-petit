package idat.damii.petit.retrofit.request

data class CorreoRequest(
    var to: String,
    var subject: String,
    var body: String
)