package fcul.cmov.voidnetwork.domain

data class Labels(
    val lables: ArrayList<Label>,
    var detectedTrees: Boolean
) {
    constructor() : this(ArrayList<Label>(), false)
}

data class Label(
    val text: String,
    val confidence: Float,
    val index: Int
)
