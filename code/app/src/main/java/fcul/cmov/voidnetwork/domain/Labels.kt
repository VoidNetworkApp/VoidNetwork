package fcul.cmov.voidnetwork.domain

data class Labels(
    var lables: ArrayList<Label>
) {
    constructor() : this(ArrayList<Label>())
}

data class Label(
    val text: String,
    val confidence: Float,
    val index: Int
)
