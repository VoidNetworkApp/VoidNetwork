package fcul.cmov.voidnetwork.domain

data class Portal(
    val street: String,
    val lat: Double,
    val lon: Double)
{
    // No-argument constructor is required for Firebase to deserialize data.
    constructor() : this("", 0.0, 0.0)
}