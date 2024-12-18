enum class CellState(
    val char: Char
) {
    GOOD('.'),
    CORRUPTED('#')
}

data class Input(
    val width: Int,
    val height: Int,
    val bytePositions: List<Vec2I>
)


fun parseInput(name: String): Input {
    val bytePositions = readInputLines(name)
        .filter { it.isNotBlank() }
        .map {
            val parts = it.split(",")
            Vec2I(parts[0].trim().toInt(), parts[1].trim().toInt())
        }

    return Input(
        bytePositions.maxOf(Vec2I::x) + 1,
        bytePositions.maxOf(Vec2I::y) + 1,
        bytePositions
    )
}

typealias MemoryMap = Maze<CellState>

fun main() {
    fun part1(input: Input, simulateSteps: Int): Int {
        val startPos = Vec2I(0, 0)
        val endPos = Vec2I(input.width - 1, input.height - 1)

        val memoryMap = MemoryMap(input.width, input.height, CellState.GOOD)
        repeat(simulateSteps) {
            val corruptedPos = input.bytePositions[it]
            memoryMap[corruptedPos] = CellState.CORRUPTED
        }

        val path = memoryMap.findShortestPathBsf(startPos, endPos, setOf(CellState.CORRUPTED))

        memoryMap.toStringWithPaths(CellState::char, listOf(path to 'O')).println()
        return path.size - 1
    }

    fun part2(input: Input, simulateSteps: Int): Any {
        val startPos = Vec2I(0, 0)
        val endPos = Vec2I(input.width - 1, input.height - 1)

        val memoryMap = MemoryMap(input.width, input.height, CellState.GOOD)
        repeat(simulateSteps) {
            val corruptedPos = input.bytePositions[it]
            memoryMap[corruptedPos] = CellState.CORRUPTED
        }

        var index = simulateSteps
        while (true) {
            val corruptedPos = input.bytePositions[index]
            memoryMap[corruptedPos] = CellState.CORRUPTED

            val path = memoryMap.findShortestPathBsf(startPos, endPos, setOf(CellState.CORRUPTED))
            if (path.isEmpty()) {
                return corruptedPos
            }

            index++
        }
    }

    part1(parseInput("day18/part1_test1"), 12).checkResult(22)
    part1(parseInput("day18/Day18"), 1024).println()

    part2(parseInput("day18/part1_test1"), 12).checkResult(Vec2I(6, 1))
    part2(parseInput("day18/Day18"), 1024).println()
}