import java.math.BigDecimal

fun main() {
    val input = readInput("Day11")
    val monkies = input.windowed(6, 7) { list ->
        list.fold(Monkey()) { monkey, s ->
            val trimmed = s.trim()
            when {
                trimmed.startsWith("Monkey") -> monkey.copy(idx = trimmed.split(" ").last().replace(":", "").toInt())
                trimmed.startsWith("Starting items:") -> monkey.copy(
                    items = trimmed.split(" ").drop(2).map { Item(it.replace(",", "").toLong()) }
                )

                trimmed.startsWith("Operation: ") -> monkey.copy(
                    op = trimmed.split(" ").takeLast(2).let { (op, target) ->
                        when {
                            target == "old" -> Pow2
                            op == "+" -> Add(target.toInt())
                            op == "*" -> Mult(target.toInt())
                            else -> TODO("$op not supported")
                        }
                    }
                )

                trimmed.startsWith("Test:") -> monkey.copy(test = trimmed.split(" ").last().toInt())
                trimmed.startsWith("If true:") -> monkey.copy(trueTarget = trimmed.split(" ").last().toInt())
                trimmed.startsWith("If false:") -> monkey.copy(falseTarget = trimmed.split(" ").last().toInt())
                else -> monkey
            }
        }
    }.let(::MonkeyState)

    val result = runMonkies(20, monkies)
    println(result.monkies.joinToString("\n"))
    println(
        result.monkies.sortedBy { it.inspectionLevel }.takeLast(2)
            .fold(1) { acc, monkey -> acc * monkey.inspectionLevel })
    val result10k = runMonkies(10000, monkies, stressed = true)
    println(result10k.monkies.joinToString("\n"))
    println(
        result10k.monkies.sortedBy { it.inspectionLevel }.takeLast(2)
            .fold(1L) { acc, monkey -> acc * monkey.inspectionLevel })
    // 21816744824
}

private fun runMonkies(times: Int, state: MonkeyState, stressed: Boolean = false): MonkeyState {
    var currentState = state
    repeat(times) {
        val size = state.monkies.size
        repeat(size) { idx ->
            currentState = runMonkey(currentState, currentState.monkies[idx], stressed)
        }
    }

    return currentState
}

private fun runMonkey(state: MonkeyState, monkey: Monkey, withStress: Boolean): MonkeyState {
    val mutableMonkies = state.monkies.toMutableList()
    monkey.items.forEach { item ->
        var worryItem = apply(op = monkey.op, item)
        worryItem = if (!withStress) apply(op = Div(3), worryItem) else worryItem
        worryItem = apply(op = state.commonDivisor, worryItem)
        val targetMonkey = if (worryItem.worryLevel % monkey.test == 0L) {
            monkey.trueTarget
        } else {
            monkey.falseTarget
        }
        mutableMonkies[targetMonkey] = mutableMonkies[targetMonkey].run { copy(items = items + worryItem) }
    }
    mutableMonkies[monkey.idx] = monkey.copy(
        items = emptyList(),
        inspectionLevel = monkey.inspectionLevel + monkey.items.size
    )
    return state.copy(monkies = mutableMonkies)
}


private data class MonkeyState(
    val monkies: List<Monkey>,
    val commonDivisor: WorryOp = Mod(monkies.fold(1) { acc, monkey -> acc * monkey.test })
)

private data class Monkey(
    val idx: Int = 0,
    val op: WorryOp = Add(0),
    val test: Int = 0,
    val trueTarget: Int = 0,
    val falseTarget: Int = 0,
    val inspectionLevel: Int = 0,
    val items: List<Item> = emptyList(),
)

private fun apply(op: WorryOp, item: Item): Item {
    return Item(
        when (op) {
            is Add -> item.worryLevel + (op.value)
            is Mult -> item.worryLevel * (op.factor)
            is Div -> item.worryLevel / (op.factor)
            Pow2 -> item.worryLevel * item.worryLevel
            is Mod -> item.worryLevel % op.factor
        }
    )
}

private data class Item(
    val worryLevel: Long
)

private sealed class WorryOp
private data class Mult(val factor: Int) : WorryOp()
private data class Add(val value: Int) : WorryOp()
private data class Div(val factor: Int = 3) : WorryOp()
private data class Mod(val factor: Int) : WorryOp()
private object Pow2 : WorryOp()
