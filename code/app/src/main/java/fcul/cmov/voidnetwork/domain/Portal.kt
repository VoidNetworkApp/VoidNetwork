package fcul.cmov.voidnetwork.domain

data class Portal(
    val id: String,
    val street: String,
    val latitude: Double,
    val longitude: Double
) {
    constructor() : this("", "", 0.0, 0.0)

    constructor(id: String, street: String, coordinates: Coordinates) :
            this(id, street, coordinates.latitude, coordinates.longitude)

    val coordinates: Coordinates
        get() = Coordinates(latitude, longitude)
}