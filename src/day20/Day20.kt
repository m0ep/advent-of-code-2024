package day20

import utils.*
import kotlin.math.abs

typealias RaceTrack = Maze<MazeTile>

data class Input(
    val map: RaceTrack,
    val start: Vec2I,
    val end: Vec2I
) {
    companion object {
        fun parse(name: String): Input {
            val map = readInput2DMapChar(name).map { it.map(MazeTile::find) }.let { RaceTrack(it) }
            val start = map.findFirst(MazeTile.START)
            val end = map.findFirst(MazeTile.END)
            return Input(map, start, end)
        }
    }
}

fun main() {
    fun part1(input: Input): Int {
        val raceTrack = input.map
        val blocker = setOf(MazeTile.WALL)
        val shortestPath = raceTrack.findShortestPathBsf(input.start, input.end, blocker)

        val result = mutableMapOf<Int, Int>()
        for (a in 0..<shortestPath.size - 1) {
            for (b in a + 1..<shortestPath.size) {
                val dist = b - a
                val positionDiff = shortestPath[a] - shortestPath[b]
                val skippedDist = abs(positionDiff.x) + abs(positionDiff.y)
                if (skippedDist <= 2) {
                    val saved = dist - skippedDist
                    if (saved >= 100) {
                        result.merge(saved, 1, Int::plus)
                    }
                }
            }
        }

        result.println()

        return result.filter { 100 <= it.key }.values.sum()
    }

    fun part2(input: Input): Int {
        val raceTrack = input.map
        val blocker = setOf(MazeTile.WALL)
        val shortestPath = raceTrack.findShortestPathBsf(input.start, input.end, blocker)

        val result = mutableMapOf<Int, Int>()
        for (a in 0..<shortestPath.size - 1) {
            for (b in a + 1..<shortestPath.size) {
                val dist = b - a
                val positionDiff = shortestPath[a] - shortestPath[b]
                val skippedDist = abs(positionDiff.x) + abs(positionDiff.y)
                if (skippedDist <= 20) {
                    val saved = dist - skippedDist
                    if (saved >= 100) {
                        result.merge(saved, 1, Int::plus)
                    }
                }
            }
        }

        result.println()
        return result.values.sum()
    }

    part1(Input.parse("day20/part1_test1")).println("Part1 test1")
    part1(Input.parse("day20/Day20")).println("Part1 input") // 1389

    part2(Input.parse("day20/part1_test1")).println("Part2 test1")
    part2(Input.parse("day20/Day20")).println("Part2 input") // 1005068
}
