package fcul.cmov.voidnetwork.domain

data class Portal(
    val street: String,
    val latitude: Double,
    val longitude: Double
) {
    constructor() : this("", 0.0, 0.0)

    constructor(street: String, coordinates: Coordinates) :
            this(street, coordinates.latitude, coordinates.longitude)

    val coordinates: Coordinates
        get() = Coordinates(latitude, longitude)
}