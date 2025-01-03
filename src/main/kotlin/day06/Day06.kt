package day06

import utils.Vec2I
import utils.println
import utils.readInputLines

typealias PuzzleMap06 = List<List<Char>>

val VEC_UP = Vec2I(0, -1)
val VEC_DOWN = Vec2I(0, 1)
val VEC_LEFT = Vec2I(-1, 0)
val VEC_RIGHT = Vec2I(1, 0)

class LoopDetectedException : Exception()

data class PuzzleInput06(
    val map: PuzzleMap06,
    val obstacles: List<Vec2I>,
    val guardStartPos: Vec2I,
    val guardStartDir: Vec2I
) {
    fun toGuard(): Guard {
        return Guard(guardStartPos, guardStartDir)
    }

    fun toMutableMap(): MutableList<MutableList<Char>> {
        return map.map { it.toMutableList() }.toMutableList()
    }
}

data class Guard(
    val pos: Vec2I,
    val direction: Vec2I
) {
    fun turnClockwise(): Guard {
        when (direction) {
            VEC_UP -> return this.copy(direction = VEC_RIGHT)
            VEC_RIGHT -> return this.copy(direction = VEC_DOWN)
            VEC_DOWN -> return this.copy(direction = VEC_LEFT)
            VEC_LEFT -> return this.copy(direction = VEC_UP)
        }

        return this.copy()
    }

    fun move() = this.copy(pos = pos + direction)
}

fun isPosInMap(
    pos: Vec2I,
    map: PuzzleMap06
): Boolean {
    if (0 > pos.y || pos.y >= map.size) {
        return false
    }

    if (0 > pos.x || pos.x >= map[0].size) {
        return false
    }

    return true
}

fun dirToChar(
    value: Vec2I
) : Char {
    when (value) {
        VEC_LEFT -> return '<'
        VEC_RIGHT -> return '>'
        VEC_UP -> return '^'
        VEC_DOWN -> return 'v'
    }

    return '?'
}


private fun parseMap(
    input: List<String>
): PuzzleInput06 {
    val map = input.map { it.toList() }

    var guardStartPos: Vec2I? = null
    var guardStartDir: Vec2I? = null
    val obstaclePositions = mutableListOf<Vec2I>()

    // find guard and obstacles
    input.withIndex().forEach({ (y, line) ->
        line.withIndex().forEach({ (x, char) ->
            if (setOf('<', '>', '^', 'v').contains(char)) {
                guardStartPos = Vec2I(x, y)

                when (char) {
                    '<' -> guardStartDir = VEC_LEFT
                    '>' -> guardStartDir = VEC_RIGHT
                    '^' -> guardStartDir = VEC_UP
                    'v' -> guardStartDir = VEC_DOWN
                }
            } else if ('#' == char) {
                obstaclePositions.add(Vec2I(x, y))
            }
        })
    })

    if (null == guardStartPos) {
        throw IllegalStateException("No guard found")
    }

    return PuzzleInput06(map, obstaclePositions, guardStartPos!!, guardStartDir!!)
}

fun simulate(
    initGuard: Guard,
    map: PuzzleMap06
): List<Guard> {
    var guard = initGuard
    val guardStates = mutableSetOf(guard)

    do {
        var nextGuard = guard.move()
        if (!isPosInMap(nextGuard.pos, map)) {
            return guardStates.toList()
        }

        val nextTile = map[nextGuard.pos.y][nextGuard.pos.x]
        if ('#' == nextTile) {
            nextGuard = guard.turnClockwise()
        } else if (guardStates.contains(nextGuard)) {
            throw LoopDetectedException()
        } else { // can move
            guardStates.add(nextGuard)
        }

        guard = nextGuard
    } while (true)
}


fun main() {
    fun part1(input: List<String>): Int {
        val puzzleMap = parseMap(input)
        val map = puzzleMap.map
        val guard = puzzleMap.toGuard()

        val simulationResult = simulate(guard, map)
        val uniquePositions = simulationResult.map { it.pos }.toSet()
        return uniquePositions.size
    }

    fun part2(input: List<String>): Int {
        val puzzleMap = parseMap(input)
        val map = puzzleMap.toMutableMap()
        val guard = puzzleMap.toGuard()

        val visitedPositions = simulate(guard, map)
            .drop(1) // remove initial position
            .map { it.pos }
            .distinct()

        var loops = 0
        for (position in visitedPositions) {
            val orgTile = map[position.y][position.x]
            map[position.y][position.x] = '#'

            try{
                simulate(guard, map)
            } catch (e: LoopDetectedException) {
                loops++
            }

            map[position.y][position.x] = orgTile
        }

        return loops
    }

    val testInput = readInputLines("day06/Day06_test")
    check(part1(testInput) == 41)

    val input = readInputLines("day06/Day06")
    val resultPart1 = part1(input)
    resultPart1.println()
    check(4665 == resultPart1)

    check(part2(testInput) == 6)

    val resultPart2 = part2(input)
    resultPart2.println()
    check(1688 == resultPart2)
}
