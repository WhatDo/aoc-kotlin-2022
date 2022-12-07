fun main() {

    val input = readInput("Day07")

    val root = Dir("/", null, emptyList())
    var shell = ShellState(FileSystem(root), root)

    var currentInput = input
    while (currentInput.isNotEmpty()) {
        val nextCmdItemCount = (currentInput.subList(1, currentInput.size)
            .indexOfFirst { it.startsWith("$") } + 1)
            .takeIf { it > 0 } ?: currentInput.size

        val cmdWithOutput = currentInput.subList(0, nextCmdItemCount)
        val cmd = parseCmd(cmdWithOutput[0])
        val cmdOutput = cmdWithOutput.subList(1, cmdWithOutput.size)

        shell = cmd.run(shell, cmdOutput)

        currentInput = currentInput.subList(cmdWithOutput.size, currentInput.size)
    }

    val smallDirs = findDirsUnder100000(root)

    println("Found small dirs $smallDirs")
    println("Found small dirs sum ${smallDirs.sumOf { it.size }}")

    val toDelete = findSmallestDirToDelete(root)
    println("Found smallest dir to delete ${toDelete.name} with size ${toDelete.size}")
}

private fun findSmallestDirToDelete(root: Dir): Dir {
    val rootSize = root.size
    val availableSize = 70000000 - rootSize
    val minDirSize = 30000000 - availableSize
    println("Missing $minDirSize")

    var smallestDir = root
    treeWalk(root) { node ->
        val size = node.size
        if (size < smallestDir.size && size > minDirSize) {
            smallestDir = node
        }
    }

    return smallestDir
}

private fun findDirsUnder100000(root: Dir): List<Dir> {
    val result = mutableListOf<Dir>()
    treeWalk(root) { node ->
        if (node.size < 100000) {
            result.add(node)
        }
    }
    return result
}

private fun treeWalk(root: Dir, onEach: (Dir) -> Unit) {
    val deque = ArrayDeque<Dir>()
    deque.add(root)

    while (deque.isNotEmpty()) {
        val node = deque.removeLast()
        node.files.forEach { if (it is Dir) deque.addLast(it) }
        onEach(node)
    }
}

data class ShellState(
    val fileSystem: FileSystem,
    val currentDir: Dir
)

class FileSystem(
    val root: Dir
)

sealed class FileSystemNode(
    val name: String,
    val parent: Dir?
) {
    abstract val size: Long
}

class File(name: String, parent: Dir, override val size: Long) : FileSystemNode(name, parent) {
    override fun toString(): String {
        return "File(name=$name)"
    }
}

class Dir(name: String, parent: Dir?, var files: List<FileSystemNode>) : FileSystemNode(name, parent) {
    override val size: Long
        get() = files.sumOf(FileSystemNode::size)

    override fun toString(): String {
        return "Dir(name=$name, files=$files)"
    }
}

sealed interface Cmd {
    fun run(state: ShellState, output: List<String>): ShellState
}

object Ls : Cmd {
    override fun run(state: ShellState, output: List<String>): ShellState {
        println("ls")
        println(output.toString())
        val currentDir = state.currentDir
        currentDir.files = output.map { parseFile(it, currentDir) }
        return state
    }
}

class Cd(private val dir: String) : Cmd {
    override fun run(state: ShellState, output: List<String>): ShellState {
        println("cd")
        if (dir == "/") {
            return state.copy(currentDir = state.fileSystem.root)
        }

        if (dir == "..") {
            return state.copy(currentDir = state.currentDir.parent ?: state.currentDir) // can't go up from root
        }

        val newDir = state.currentDir.files.find { it.name == dir }
        if (newDir is Dir) {
            return state.copy(currentDir = newDir)
        }

        TODO("Unknown argument $dir")
    }
}


private fun parseFile(file: String, dir: Dir) = file.split(" ").let { (type, name) ->
    when {
        type == "dir" -> Dir(name, dir, emptyList())
        numberRegex.matches(type) -> File(name, dir, type.toLong())
        else -> TODO("Unknown output type $type")
    }
}

private fun parseCmd(cmd: String): Cmd = cmd.split(" ").let { line ->
    when (val cmd = line[1]) {
        "cd" -> Cd(line[2])
        "ls" -> Ls
        else -> TODO("Unknown cmd $cmd")
    }
}

private val numberRegex = "^[0-9]+$".toRegex()