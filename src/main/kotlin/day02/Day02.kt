package day02

import utils.readInputLines
import kotlin.math.abs

enum class Ordering {
    ASCENDING,
    DESCENDING
}

fun isReportSafe(
    report: List<Int>): Boolean {
    val ordering = if (report[0] < report[1]) Ordering.ASCENDING else Ordering.DESCENDING
    for ((current, next) in report.windowed(2, 1)) {
        val diff = abs(next - current)

        if (diff !in 1..3) {
            return false
        } else if (ordering == Ordering.ASCENDING && current > next) {
            return false
        } else if (ordering == Ordering.DESCENDING && current < next) {
            return false
        }
    }

    return true
}

fun canBeMadeSafe(report: List<Int>): Boolean {
    return (0 until report.count())
        .any{ i -> isReportSafe(report.filterIndexed { index, _ -> index != i }) }
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.count {
            it.split(" ").map(String::toInt).let(::isReportSafe)
        }
    }

    fun part2(input: List<String>): Int {
        return input.count {
            it.split(" ").map(String::toInt).let(::canBeMadeSafe)
        }
    }

    check(part1(listOf("7 6 4 2 1")) == 1)
    check(part1(listOf("1 3 6 7 9")) == 1)
    check(part1(listOf("1 2 7 8 9")) == 0)
    check(part1(listOf("8 6 4 4 1")) == 0)

    val testInput = readInputLines("day02/Day02_test")
    check(part1(testInput) == 2)

    val input = readInputLines("day02/Day02")
    part1(input).also(::println).also { check(479 == it) }
    part2(input).also(::println).also { check(531 == it) }
}
