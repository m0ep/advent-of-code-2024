package day04

import println
import readInputLines

fun main() {

    fun <T> validPos(list: List<List<T>>, x: Int, y: Int): Boolean {
        return y >= 0 && y < list.size
                && x >= 0 && x < list[y].size
    }

    fun findPart1(
        haystack: List<List<Char>>,
        x: Int,
        y: Int,
        dirX: Int,
        dirY: Int
    ): Boolean {
        val needle = "XMAS"

        for (i in needle.indices) {
            val wndX = x + i * dirX
            val wndY = y + i * dirY

            if (validPos(haystack, wndX, wndY)) {
                val element = haystack[wndY][wndX]
                if (needle[i] != element) {
                    return false
                }
            } else {
                return false
            }
        }

        return true
    }

    fun part1(input: List<String>): Int {
        val haystack = input.map { it.toList() }.toList()
        var count = 0
        for (y in haystack.indices) {
            for (x in haystack[y].indices) {
                val c = haystack[y][x]
                if ('X' == c) {
                    for (dirY in -1..1) {
                        for (dirX in -1..1) {

                            if (findPart1(haystack, x, y, dirX, dirY)) {
                                count++
                            }
                        }
                    }
                }
            }
        }

        return count
    }

    fun part2(input: List<String>): Int {
        val haystack = input.map { it.toList() }.toList()
        var count = 0

        val validPattern = setOf("MMSS", "SMSM", "SSMM", "MSMS")
        for (y in haystack.indices) {
            for (x in haystack[y].indices) {
                val c = haystack[y][x]
                if ('A' == c) {
                    if (!validPos(haystack, x - 1, y - 1)
                        || !validPos(haystack, x + 1, y - 1)
                        || !validPos(haystack, x - 1, y + 1)
                        || !validPos(haystack, x + 1, y + 1)
                    ) {
                        continue
                    }

                    val topLeft = haystack[y - 1][x - 1]
                    val topRight = haystack[y - 1][x + 1]
                    val bottomLeft = haystack[y + 1][x - 1]
                    val bottomRight = haystack[y + 1][x + 1]

                    val test = "$topLeft$topRight$bottomLeft$bottomRight"
                    if(validPattern.contains(test)){
                        count++
                    }
                }
            }
        }

        return count
    }

    val inputTest1 = readInputLines("day04/Day04_test1")
    check(part1(inputTest1) == 5)

    val inputTest2 = readInputLines("day04/Day04_test2")
    check(part1(inputTest2) == 18)

    val input = readInputLines("day04/Day04")
    part1(input).println()

    val inputTest3 = readInputLines("day04/Day04_test3")
    check(part2(inputTest3) == 1)

    val inputTest4 = readInputLines("day04/Day04_test4")
    check(part2(inputTest4) == 9)

    part2(input).println()
}
