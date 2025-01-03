package day19

import utils.checkResult
import utils.println
import utils.readInputLines

data class Input(
    val patterns: Set<String>,
    val designs: List<String>
) {
    companion object {
        fun parse(name: String): Input {
            val lines = readInputLines(name)
            val patterns = lines.takeWhile { it.isNotBlank() }
                .flatMap { it.split(",").map(String::trim) }
                .toSet()

            val designs = lines.dropWhile(String::isNotBlank)
                .filter(String::isNotBlank)

            return Input(patterns, designs)
        }
    }
}

fun isDesignPossible(
    design: String,
    patterns: Set<String>,
    possible: MutableMap<String, Boolean>
): Boolean {
    if (design.isEmpty()) {
        return true
    } else if (design in possible) {
        return possible[design] ?: false
    }

    for (pattern in patterns) {
        if (pattern.length <= design.length && design.startsWith(pattern)) {
            if (isDesignPossible(design.substring(pattern.length), patterns, possible)) {
                possible[design] = true
                return true
            }
        }
    }

    possible[design] = false
    return false
}

fun countArrangements(
    design: String,
    patterns: Set<String>,
    arrangements: MutableMap<String, ULong>
): ULong {
    if (design.isEmpty()) {
        return 1UL
    } else if (design in arrangements) {
        return arrangements[design] ?: 0UL
    }

    var result = 0UL
    for (pattern in patterns) {
        if (pattern.length <= design.length && design.startsWith(pattern)) {
            result += countArrangements(design.substring(pattern.length), patterns, arrangements)
        }
    }

    arrangements[design] = result
    return result
}

fun main() {
    fun part1(input: Input): Int {
        var result = 0
        val possible = mutableMapOf<String, Boolean>()
        for (design in input.designs) {
            if (isDesignPossible(design, input.patterns, possible)) {
                result++
            }
        }

        return result
    }

    fun part2(input: Input): ULong {
        var result = 0UL
        val startsWith = mutableMapOf<String, ULong>()
        for (design in input.designs) {
            result += countArrangements(design, input.patterns, startsWith)
        }

        return result
    }


    part1(Input.parse("Day19/part1_test1")).checkResult(6)
    part1(Input.parse("Day19/Day19")).println("Part1")

    part2(Input.parse("Day19/part1_test1")).checkResult(16UL)
    part2(Input.parse("Day19/Day19")).println("Part2")
}
