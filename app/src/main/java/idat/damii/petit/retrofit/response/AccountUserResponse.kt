package idat.damii.petit.retrofit.response

import idat.damii.petit.retrofit.request.AccountUserRequest

data class AccountUserResponse(
    var status: Int,
    var message: String,
    var body: AccountUserRequest?
)