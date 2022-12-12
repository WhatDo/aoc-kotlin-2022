fun main() {
    val input = readInput("Day09")


    val rope2 = moveRope(RopeState.create(2), input)
    println(rope2.tailHistory.size)

    val rope10 = moveRope(RopeState.create(10), input)
    println(rope10.tailHistory.size)
    //10553 high
}

private fun moveRope(state: RopeState, input: List<String>): RopeState {
    return input.fold(state) { state, s ->
        val move = getMove(s)
        moveRope(state, move)
    }
}

fun moveRope(state: RopeState, move: Vec2): RopeState {
//    println(move)
    var toMove = move
    var newState = state
    while (toMove.size > 0) {
        val move1 = toMove.normalize()
        val newHead = newState.head + move1
        val newTail = moveTailToHead(newHead, newState.tail)
        newState = newState.copy(rope = newTail, tailHistory = newState.tailHistory + newTail.last())
        toMove -= move1
//        println(newState)
    }
    return newState
}

fun moveTailToHead(newHead: Vec2, tails: List<Vec2>): List<Vec2> {
    return tails.scan(newHead) { head, tail ->
        if (head.distanceTo(tail) > 1) {
            val toMove = (head - tail).normalize()
            tail + toMove
        } else {
            tail
        }
    }
}

data class RopeState(
    val rope: List<Vec2>,
    val tailHistory: Set<Vec2> = rope.toSet()
) {
    val head get() = rope.first()
    val tail get() = rope.subList(1, rope.size)
    override fun toString(): String {
        return "RopeState($rope)"
    }

    companion object {
        fun create(size: Int): RopeState {
            return RopeState(
                List(size) { Vec2(0, 0) }
            )
        }
    }
}

private fun getMove(string: String): Vec2 {
    val (dir, amountStr) = string.split(" ")
    val amount = amountStr.toInt()
    return when (dir) {
        "R" -> Vec2(amount, 0)
        "L" -> Vec2(-amount, 0)
        "U" -> Vec2(0, amount)
        "D" -> Vec2(0, -amount)
        else -> TODO("No move for $dir")
    }
}