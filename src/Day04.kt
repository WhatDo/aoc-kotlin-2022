fun main() {
    val input = readInput("Day04")
    val rangePairs = input.map(::mapPairs)

    val fullOverlapping = rangePairs
        .filter { (r1, r2) -> hasFullOverlap(r1, r2) }
        .onEach { (r1, r2) -> println("$r1 and $r2 is fully overlapping") }

    println("There are ${fullOverlapping.size} overlaps")

    val partialOverlapping = rangePairs
        .filter { (r1, r2) -> hasPartialOverlap(r1, r2) }
        .onEach { (r1, r2) -> println("$r1 and $r2 is partially overlapping") }

    println("There are ${partialOverlapping.size} partial overlaps")
}

private fun hasPartialOverlap(range1: IntRange, range2: IntRange) = range1.contains(range2) || range2.contains(range1)

private fun hasFullOverlap(range1: IntRange, range2: IntRange) =
    range1.fullyContains(range2) || range2.fullyContains(range1)

private fun mapPairs(row: String): Pair<IntRange, IntRange> {
    return row.split(",")
        .map { range ->
            val (low, high) = range.split("-").map { it.toInt() }
            low..high
        }.let { (first, second) -> first to second }
}