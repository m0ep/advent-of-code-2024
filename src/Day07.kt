@file:OptIn(ExperimentalUnsignedTypes::class)

private data class Equation(
    val result: ULong,
    val operants: List<ULong>
)

fun btreeLeftIdx(idx: Int): Int = 2 * idx + 1
fun btreeRightIdx(idx: Int): Int = 2 * idx + 2

private fun parseInput(
    input: List<String>
): List<Equation> {
    return input.map { line ->
        val result = line.substringBefore(":").toULong()
        val operants = line.substringAfter(": ").split(" ").map { it.toULong() }.toList()
        Equation(result, operants)
    }
}

fun concat(left: ULong, right: ULong):ULong = "$left$right".toULong()

private fun isSolvablePart1(
    equation: Equation
): Boolean {
    val operants = equation.operants
    val btree = ULongArray(operants.size.pow(2)-1)
    btree[0] = operants[0]
    for (level in 0 until operants.size - 1) {
        val lastLevel = level == operants.size - 2

        val levelStartIdx = level.pow(2) - 1
        for (idx in 0 until level.pow(2)) {

            val leftIdx = btreeLeftIdx(levelStartIdx + idx)
            btree[leftIdx] = btree[levelStartIdx + idx] + operants[level + 1]
            if(lastLevel && btree[leftIdx] == equation.result) {
                return true
            }

            val rightIdx = btreeRightIdx(levelStartIdx + idx)
            btree[rightIdx] = btree[levelStartIdx + idx]  * operants[level + 1]
            if(lastLevel && btree[rightIdx] == equation.result) {
                return true
            }
        }
    }

    return false
}

private fun isSolvablePart2(
    equation: Equation
) : Boolean{

    val operant = equation.operants[0]
    val operantLeft = equation.operants.drop(1)
    return isSolvablePart2DepthSolve(equation.result, operantLeft, operant)
}

private fun isSolvablePart2DepthSolve(
    targetValue: ULong,
    operants: List<ULong>,
    parentValue: ULong
) : Boolean {
    val operant = operants[0]
    val operantLeft = operants.drop(1)

    val values = listOf(
        parentValue + operant,
        parentValue * operant,
        concat(parentValue, operant)
    )

    return if(operantLeft.isNotEmpty()){
        values.any { isSolvablePart2DepthSolve(targetValue, operantLeft, it) }
    } else {
        values.any { it == targetValue }
    }
}

fun main() {
    fun part1(input: List<String>): ULong {
        return parseInput(input)
            .filter(::isSolvablePart1)
            .sumOf { it.result }
    }

    fun part2(input: List<String>): ULong {
        return parseInput(input)
            .filter(::isSolvablePart2)
            .sumOf { it.result }
    }

    part1(listOf("3267: 81 40 27")).checkResult(3267UL)
    part1(listOf("190: 10 19")).checkResult(190UL)

    val inputTest = readInputLines("day07/Day07_test")
    part1(inputTest).checkResult(3749UL)

    val input = readInputLines("day07/Day07")
    part1(input).println("Part1")

    part2(inputTest).checkResult(11387UL)
    part2(input).println("Part2")
}


