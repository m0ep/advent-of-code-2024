package day03

import utils.println
import utils.readInputString

fun main() {
    fun part1(input: String): Int {
        val regex = Regex("mul\\(([0-9]+),([0-9]+)\\)")

        var result = 0
        regex.findAll(input).forEach {
            val (a, b) = it.destructured
            result += a.toInt() * b.toInt()
        }

        return result
    }

    fun part2(input: String): Int {
        val regex = Regex("do\\(\\)|don't\\(\\)|mul\\(([0-9]+),([0-9]+)\\)")

        var result = 0
        var shouldDo = true
        regex.findAll(input).forEach {
            if("do()" == it.value){
                shouldDo = true
            } else if("don't()" == it.value){
                shouldDo = false
            } else if(shouldDo) {
                val (a, b) = it.destructured
                result += a.toInt() * b.toInt()
            }
        }

        return result
    }

    check(part1("mul(5,5)") == 25)
    check(part1("mul ( 2 , 4 )") == 0)
    check(part1("mul(6,9!") == 0)

    val input = readInputString("day03/Day03")
    part1(input).println()
    part2(input).println()
}
