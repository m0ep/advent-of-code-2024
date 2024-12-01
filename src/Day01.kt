import kotlin.math.abs

data class ParsedInput(
    val left: List<Int>,
    val right: List<Int>
)

fun parseInput(input: List<String>): ParsedInput {
    val left = mutableListOf<Int>()
    val right = mutableListOf<Int>()

    for (line in input) {
        val elements = line.split(" ", limit = 2)
            .map { it.trim() }
            .map { it.toInt() }

        left.add(elements[0])
        right.add(elements[1])
    }

    return ParsedInput(left, right)
}

fun main() {
    fun part1(input: List<String>): Int {
        val (left, right) = parseInput(input)

        check(left.size == right.size) { "Left and right should have the same size" }
        val leftSorted = left.sorted()
        val rightSorted = right.sorted()

        var result = 0
        for (i in left.indices) {
            result += abs(rightSorted[i] - leftSorted[i])
        }

        return result
    }

    fun part2(input: List<String>): Int {
        val (left, right) = parseInput(input)

        val rightCountedMap = mutableMapOf<Int, Int>()
        for (rValue in right) {
            rightCountedMap[rValue] = rightCountedMap.getOrDefault(rValue, 0) + 1
        }

        var result = 0
        for (lValue in left) {
            result += (lValue * rightCountedMap.getOrDefault(lValue, 0))
        }

        return result
    }

    // Test if implementation meets criteria from the description, like:
    check(part1(listOf("3   7")) == 4)
    check(part1(listOf("9   3")) == 6)

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 11)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    "Day01 Part 1 ----".println()
    part1(input).println()


    check(part2(testInput) == 31)

    "Day01 Part 2 ----".println()
    part2(input).println()
}