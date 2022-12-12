import kotlin.math.abs
import kotlin.math.sign

fun main() {
    val input = readInput("Day08")
    val grid = Grid.create(input)

    val visibleTrees = grid.map.entries.filter { entry ->
        entry.isVisibleIn(grid)
    }

    println("visible tree count ${visibleTrees.size}")

    val bestScenic = grid.map.entries.maxBy { it.scenicScore(grid) }
    val score = bestScenic.scenicScore(grid)

    println("best scenic tree is $bestScenic with score $score")
}

private fun TreeEntry.lookOut(grid: Grid): Array<List<Vec2>> {
    val (x, y) = key
    val left = (x - 1) downTo 1
    val right = (x + 1)..grid.size.first
    val up = (y - 1) downTo 1
    val down = (y + 1)..grid.size.second

    return arrayOf(left.map { it to y }, right.map { it to y }, up.map { x to it }, down.map { x to it })
}

private fun TreeEntry.scenicScore(grid: Grid): Int {
    val directions = lookOut(grid)
    return directions.fold(1) { acc, direction ->
        var index = direction.indexOfFirst { vec -> grid.map[vec].let { tree -> tree == null || tree >= value } }
        if (index == -1) {
            index = if (direction.isNotEmpty()) {
                val (lastX, lastY) = direction.last()
                abs(lastX - key.first) + abs(lastY - key.second)
            } else {
                0
            }
        } else {
            index++
        }
        acc * index
    }
}

private fun TreeEntry.isVisibleIn(grid: Grid): Boolean {
    val directions = lookOut(grid)
    val treeHeight = value
    return directions.any { trees -> trees.all { tree -> grid.map[tree].let { it == null || key == tree || it < treeHeight } } }
}


typealias TreeMap = Map<Vec2, Int>
typealias TreeEntry = Map.Entry<Vec2, Int>

private class Grid(val map: TreeMap, val size: Vec2) {

    companion object {
        fun create(input: List<String>): Grid {
            val map = input.withIndex().flatMap { row ->
                row.value.withIndex().map { col -> (col.index + 1 to row.index + 1) to col.value.digitToInt() }
            }.toMap()

            val height = input.size
            val width = input[0].length


            return Grid(map, width to height)
        }
    }
}