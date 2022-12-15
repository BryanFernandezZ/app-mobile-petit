package idat.damii.petit.model

data class Usuario (
    var id: Int?,
    var names: String,
    var lastNames: String,
    var dni: String,
    var phone: String,
    var photo: String
)