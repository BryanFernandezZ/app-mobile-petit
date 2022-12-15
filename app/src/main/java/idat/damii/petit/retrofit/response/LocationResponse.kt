package idat.damii.petit.retrofit.response

import idat.damii.petit.retrofit.request.LocationRequest

data class LocationResponse(
    var status: Int,
    var message: String,
    var body: LocationRequest?,
)
