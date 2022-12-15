package idat.damii.petit.retrofit.request

data class AccountUserRequest(
    var idUser: Int?,
    var names: String,
    var lastNames: String,
    var dni: String,
    var phone: String,
    var photo: String,
    var idAccount: Int?,
    var email: String,
    var password: String,
    var idAccountType: Int
)
