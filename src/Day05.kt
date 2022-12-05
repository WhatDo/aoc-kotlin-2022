fun main() {
    val input = readInput("Day05")
    val crates = parseCrates(input)
    println(crates)
    val instructions = parseInstructions(input)
    println(instructions)

    instructions.forEach { instr ->
        when (instr) {
            is Move -> move9001(crates, instr)
        }
    }

    val sortedStacks = crates.stacks.entries.sortedBy { it.key }
    println("Top crates are ${sortedStacks.joinToString(separator = "") { it.value.lastOrNull()?.toString() ?: " " }}")
}

private fun move(inv: Inventory, move: Move) {
    repeat(move.amount) {
        inv.stacks[move.to]!!.addLast(inv.stacks[move.from]!!.removeLast())
    }
}

private fun move9001(inv: Inventory, move: Move) {
    val from = inv.stacks[move.from]!!
    val to = inv.stacks[move.to]!!
    val moveIdx = from.size - move.amount
    repeat(move.amount) {
        to.addLast(from.removeAt(moveIdx))
    }
}

sealed class Instr
data class Move(val amount: Int, val from: Int, val to: Int) : Instr()

data class Inventory(
    val stacks: Map<Int, ArrayDeque<Char>>
)

private fun parseInstructions(input: List<String>): List<Instr> {
    return input.subList(input.indexOf("") + 1, toIndex = input.size)
        .map { instr -> instr.split(" ") }
        .map { instrList -> Move(instrList[1].toInt(), instrList[3].toInt(), instrList[5].toInt()) }
}

private fun parseCrates(input: List<String>): Inventory {
    val stacks = input.takeWhile { !numberRowRegex.matches(it) }.map { row ->
        row.windowed(3, 4, partialWindows = true) { crate ->
            if (crateRegex.matches(crate)) {
                crate[1]
            } else {
                null
            }
        }
    }.fold(mutableMapOf<Int, ArrayDeque<Char>>()) { map, chars ->
        chars.forEachIndexed { index, c ->
            if (c != null) {
                map.getOrPut(index + 1) { ArrayDeque() }.addFirst(c)
            }
        }

        map
    }

    return Inventory(stacks)
}


val crateRegex = "^\\[[A-Z]\\]$".toRegex()
val numberRowRegex = "^[1-9\\s]+$".toRegex()