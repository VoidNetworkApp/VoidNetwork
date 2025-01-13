package fcul.cmov.voidnetwork.domain

data class Coordinates(val latitude: Double, val longitude: Double) {
    override fun toString(): String {
        return "($latitude,$longitude)"
    }
}