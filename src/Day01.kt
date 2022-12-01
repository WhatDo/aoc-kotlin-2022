fun main() {
    val input = readInput("Day01")

    val (maxCalories, _) = input.fold(0 to 0) { (top, sum), s ->
        if (s.isBlank()) {
            maxOf(top, sum) to 0
        } else {
            top to sum + s.toInt()
        }
    }

    println("Highest sum of calories: $maxCalories")


    val (topThree, _) = input.fold(TopThree() to 0) { (top, sum), s ->
        if (s.isBlank()) {
            top.with(sum) to 0
        } else {
            top to sum + s.toInt()
        }
    }

    println("Sum of the top three calories: ${topThree.values.sum()}")
}


class TopThree(
    val values: List<Int> = mutableListOf(0, 0, 0)
) {
    fun with(x0: Int) = TopThree((values + x0).sortedDescending().take(3))
}