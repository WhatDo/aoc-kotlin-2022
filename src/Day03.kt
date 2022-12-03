fun main() {
    val rucksacks = readInput("Day03").map { rucksack ->
        val (first, second) = rucksack
            .toList()
            .let { list ->
                list.subList(0, rucksack.length / 2) to list.subList(rucksack.length / 2, list.size)
            }
        Rucksack(Compartment(first.toSet()), Compartment(second.toSet()))
    }

    val summedPriority = rucksacks.sumOf { rucksack ->
        rucksack.first.overlap(rucksack.second).also {
            println("${rucksack.first.items} overlaps with ${rucksack.second.items} by $it (${it.first().priority})")
        }.sumOf { it.priority }
    }

    println("Total sum of priority is $summedPriority")

    val badges = rucksacks.windowed(3, 3) { sacks ->
        sacks.fold(sacks.first().allItems) { acc, sack ->
            acc.intersect(sack.allItems).also { println(sack.allItems) }
        }.also {
            println("Common item is $it")
        }
    }.sumOf { it.sumOf { it.priority } }

    println("Sum of badge icons is $badges")

}

data class Rucksack(
    val first: Compartment,
    val second: Compartment
) {
    val allItems = first.items + second.items
}

class Compartment(
    val items: Set<Char>
)

fun Compartment.overlap(other: Compartment) = items.intersect(other.items)

val Char.priority
    get() = if (isLowerCase()) {
        code - 'a'.code + 1
    } else {
        code - 'A'.code + 27
    }

