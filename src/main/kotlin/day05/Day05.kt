package day05

import utils.println
import utils.readInputLines

data class PuzzleInput(
    val orderingMap: Map<Int, Set<Int>>,
    val updates: List<List<Int>>
)

fun parsePuzzleInput(
    input: List<String>
): PuzzleInput {
    val orderingMap = mutableMapOf<Int, MutableSet<Int>>()
    var updateStartIndex = 0
    for ((index, line) in input.withIndex()) {
        if (line.isBlank()) {
            updateStartIndex = index + 1
            break
        } else {
            val ordering = line.split("|")
                .map { it.trim().toInt() }
                .toIntArray()

            orderingMap.getOrPut(ordering[0]) { mutableSetOf() }.add(ordering[1])
        }
    }
    val updates = mutableListOf<MutableList<Int>>()
    for (line in input.drop(updateStartIndex)) {
        val update = line.split(",")
            .map { it.trim().toInt() }
            .toMutableList()

        updates.add(update)
    }

    return PuzzleInput (orderingMap, updates)
}

fun hasRightOrdering(
    update: List<Int>,
    orderingMap: Map<Int, Set<Int>>
): Boolean {
    for ((index, value) in update.withIndex()) {
        val ordering = orderingMap[value] ?: continue

        for (checkIndex in index - 1 downTo 0) {
            if (ordering.contains(update[checkIndex])) {
                return false
            }
        }
    }

    return true
}

fun fixOrdering(
    update: List<Int>,
    orderingMap: Map<Int, Set<Int>>
): List<Int> {

    return update
        .sortedWith { o1, o2 ->
            if (orderingMap[o1]?.contains(o2) == true) -1
            else if (orderingMap[o2]?.contains(o1) == true) 1
            else 0
        }
}

fun main() {
    fun part1(input: List<String>): Int {
        val puzzleInput = parsePuzzleInput(input)

        return puzzleInput.updates
            .filter { hasRightOrdering(it, puzzleInput.orderingMap) }
            .sumOf { it[Math.round(it.size / 2.0).toInt() - 1] }
    }

    fun part2(input: List<String>): Int {
        val puzzleInput = parsePuzzleInput(input)

        return puzzleInput.updates
            .filterNot { hasRightOrdering(it, puzzleInput.orderingMap) }
            .map { fixOrdering(it, puzzleInput.orderingMap) }
            .sumOf { it[Math.round(it.size / 2.0).toInt() - 1] }
    }

    val part1TestResult = part1(readInputLines("day05/Day05_test"))
    "Part1 test result = $part1TestResult".println()
    check(part1TestResult == 143)

    val input = readInputLines("day05/Day05")
    part1(input).println()

    val part2TestResult = part2(readInputLines("day05/Day05_test"))
    "Part2 test result = $part2TestResult".println()
    check(part2TestResult == 123)

    part2(input).println()
}
