package idat.damii.petit.model

data class Cita(
    var idQuote: Int?,
    var dateIssued: String?,
    var dateAttention: String?,
    var price: Double?,
    var serviceType: ServiceType?,
    var state: State?
)
