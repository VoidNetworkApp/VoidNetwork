package fcul.cmov.voidnetwork.domain

data class Coordinates(val latitude: Double, val longitude: Double) {
    override fun toString(): String {
        return "($latitude,$longitude)"
    }

    companion object {
        fun fromString(coordinatesStr: String?): Coordinates? {
            if (coordinatesStr == null) return null
            val coordinates = coordinatesStr.removePrefix("(").removeSuffix(")").split(",")
            return Coordinates(coordinates[0].toDouble(), coordinates[1].toDouble())
        }
    }
}