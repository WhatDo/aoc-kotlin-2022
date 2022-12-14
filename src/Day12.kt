import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val input = readInput("Day12")
    val graph = Graph.create(input)

    val time = measureTime {
        val start = requireNotNull(graph.nodes.find { it.value == 'S' })
        val stepsFromStart = bfs(graph, start)
        println("Steps from 'S' to 'E' = $stepsFromStart")
    }

    println("took $time")

    val time2 = measureTime {
        val shortest = graph.nodes.filter { node -> node.value.asElevation() == 'a' }
            .map { bfs(graph, it) }
            .filter { it != -1 }
            .min()

        println("Shortest trail is $shortest steps")
    }

    println("took $time2")
}


private fun bfs(graph: Graph, start: Node): Int {
    val visited = mutableSetOf(start)
    val queue = ArrayDeque<Pair<Int, Node>>(graph.nodes.size)
    queue.addLast(0 to start)
    val path = mutableMapOf<Node, Node>()

    while (queue.isNotEmpty()) {
        val (step, node) = queue.removeFirst()
        if (node.value == 'E') {
            return step
        }

        node.neighbors(graph)
            .filter(node::canGoTo)
            .filter(visited::add)
            .forEach { neighbor ->
                queue.addLast(step + 1 to neighbor)
                path[neighbor] = node
            }
    }

    return -1
}

private class Graph(val nodes: List<Node>, val width: Int, val height: Int) {

    fun get(x: Int, y: Int): Node? {
        if (x !in 0 until width || y !in 0 until height) {
            return null
        }

        return nodes[getIndex(x, y)]
    }

    private fun getIndex(x: Int, y: Int): Int {
        return x + y * width
    }

    companion object {
        fun create(graph: List<String>): Graph {
            val width = graph[0].length
            val height = graph.size

            val nodes = graph.flatMapIndexed { y, row -> row.mapIndexed { x, c -> Node(c, x, y) } }

            return Graph(nodes, width, height)
        }
    }
}


private data class Node(val value: Char, val x: Int, val y: Int)

private fun Node.neighbors(graph: Graph): List<Node> {
    return listOf(x - 1 to y, x + 1 to y, x to y - 1, x to y + 1)
        .mapNotNull { (x, y) -> graph.get(x, y) }
}

private fun Node.canGoTo(other: Node) = (value.asElevation() - other.value.asElevation()) >= -1

private fun Char.asElevation() =
    when (this) {
        'S' -> 'a'
        'E' -> 'z'
        else -> this
    }
