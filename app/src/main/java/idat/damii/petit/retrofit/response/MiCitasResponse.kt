package idat.damii.petit.retrofit.response

import idat.damii.petit.model.ServiceType
import idat.damii.petit.model.State

data class MiCitasResponse(
    var id: Int?,
    var dateIssued: String,
    var dateAttention: String,
    var price: Double,
    var serviceType: ServiceType,
    var state: State
)
