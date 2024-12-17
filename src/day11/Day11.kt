package day11

import checkResult
import println
import readInputString

fun countDigits(
    value: ULong
): Int {
    var digits = 1
    var tmp = value
    while (9UL < tmp) {
        digits++
        tmp /= 10UL
    }

    return digits
}

fun splitInt(
    value: ULong,
    afterDigit: Int
): Pair<ULong, ULong> {
    var left = value
    var mul = 1UL
    var right = 0UL
    for (i in 0 until afterDigit) {
        val tmp = left % 10UL
        left /= 10UL
        right += tmp * mul
        mul *= 10UL
    }

    return (left) to (right)
}

fun simulatePart1SingleBlink(
    stones: MutableList<ULong>
): MutableList<ULong> {
    val nextStones = mutableListOf<ULong>()
    for (idx in stones.indices) {
        val stone = stones[idx]
        if (0UL == stone) {
            nextStones.add(1UL)
        } else {
            val numDigits = countDigits(stone)
            if (0 == numDigits % 2) {
                val split = splitInt(stone, numDigits / 2)
                nextStones.add(split.first)
                nextStones.add(split.second)
            } else {
                nextStones.add(stone * 2024UL)
            }
        }
    }
    return nextStones
}

fun simulatePart2SingleBlink(
    stones: Map<ULong, ULong>
): Map<ULong, ULong> {
    val result = mutableMapOf<ULong, ULong>()
    for ((stone, count) in stones) {
        if (0UL == stone) {
            result.merge(1UL, count, ULong::plus)
        } else {
            val numDigits = countDigits(stone)
            if (0 == numDigits % 2) {
                val split = splitInt(stone, numDigits / 2)
                result.merge(split.first, count, ULong::plus)
                result.merge(split.second, count, ULong::plus)
            } else {
                result.merge(stone * 2024UL, count, ULong::plus)
            }
        }
    }

    return result
}

fun main() {
    fun simulatePart1(
        input: String,
        blinks: Int
    ): Int {
        val stones = input.split(" ").map { it.toULong() }.toMutableList()
        return (0..<blinks)
            .fold(stones) { acc, _ -> simulatePart1SingleBlink(acc) }
            .size
    }

    fun simulatePart2(
        input: String,
        blinks: Int
    ): ULong {
        val stones = input.split(" ").map { it.toULong() }.toMutableList()

        val stoneCounts = stones.groupingBy { it }
            .fold(0UL) { acc, _ -> acc + 1UL }

        return (0..<blinks)
            .fold(stoneCounts) { acc, _ -> simulatePart2SingleBlink(acc) }
            .values
            .sum()
    }

    simulatePart1("0 1 10 99 999", 1).checkResult(7)
    simulatePart1("125 17", 6).checkResult(22)
    simulatePart1("125 17", 25).checkResult(55312)

    val input = readInputString("day11/Day11")
    simulatePart1(input, 25).println()
    simulatePart2(input, 75).println()
}
