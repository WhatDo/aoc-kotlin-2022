

fun main() {
    val input = readInput("Day06")[0]

    val startMarker = input.markerAfterDistinctChars(4)
    println("First marker after character $startMarker")

    val messageMarker = input.markerAfterDistinctChars(14)
    println("Message marker after character $messageMarker")
}

fun String.markerAfterDistinctChars(distinct: Int): Int {
    return windowed(distinct, 1).withIndex().first { it.value.toSet().size == distinct }.index + distinct
}