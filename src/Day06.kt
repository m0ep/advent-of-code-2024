typealias PuzzleMap06 = List<List<Char>>

val VEC_UP = Vec2(0, -1)
val VEC_DOWN = Vec2(0, 1)
val VEC_LEFT = Vec2(-1, 0)
val VEC_RIGHT = Vec2(1, 0)

data class PuzzleInput06(
    val map: PuzzleMap06,
    val obstacles: List<Vec2>,
    val guardStartPos: Vec2,
    val guardStartDir: Vec2
) {
    fun toGuard(): Guard {
        return Guard(guardStartPos, guardStartDir)
    }

    fun toMutableMap(): MutableList<MutableList<Char>> {
        return map.map { it.toMutableList() }.toMutableList()
    }
}

data class Guard(
    val pos: Vec2,
    val direction: Vec2
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
    pos: Vec2,
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

fun charToDir(
    value: Char
) : Vec2 {
    when (value) {
        '<' -> return VEC_LEFT
        '>' -> return VEC_RIGHT
        '^' -> return VEC_UP
        'v' -> return VEC_DOWN
    }

    return Vec2(0, 0)
}

fun dirToChar(
    value: Vec2
) : Char {
    when (value) {
        VEC_LEFT -> return '<'
        VEC_RIGHT -> return '>'
        VEC_UP -> return '^'
        VEC_DOWN -> return 'v'
    }

    return '?'
}


fun parseMap(
    input: List<String>
): PuzzleInput06 {
    val map = input.map { it.toList() }

    var guardStartPos: Vec2? = null
    var guardStartDir: Vec2? = null
    val obstaclePositions = mutableListOf<Vec2>()

    // find guard and obstacles
    input.withIndex().forEach({ (y, line) ->
        line.withIndex().forEach({ (x, char) ->
            if (setOf('<', '>', '^', 'v').contains(char)) {
                guardStartPos = Vec2(x, y)

                when (char) {
                    '<' -> guardStartDir = VEC_LEFT
                    '>' -> guardStartDir = VEC_RIGHT
                    '^' -> guardStartDir = VEC_UP
                    'v' -> guardStartDir = VEC_DOWN
                }
            } else if ('#' == char) {
                obstaclePositions.add(Vec2(x, y))
            }
        })
    })

    if (null == guardStartPos) {
        throw IllegalStateException("No guard found")
    }

    return PuzzleInput06(map, obstaclePositions, guardStartPos!!, guardStartDir!!)
}

fun main() {
    fun part1(input: List<String>): Int {
        var puzzleMap = parseMap(input)

        val map = puzzleMap.toMutableMap()
        var guard = puzzleMap.toGuard()
        val moves = mutableListOf(guard)
        val visitedPositions = mutableSetOf(guard.pos)

        do {
            var nextGuard = guard.move()
            if(!isPosInMap(nextGuard.pos, map)) {
                map[guard.pos.y][guard.pos.x] = 'X'
                break
            }

            val nextTile = map[nextGuard.pos.y][nextGuard.pos.x]
            if('#' == nextTile){
                nextGuard = guard.turnClockwise()
                map[nextGuard.pos.y][nextGuard.pos.x] = dirToChar(nextGuard.direction)
            } else { // can move
                map[guard.pos.y][guard.pos.x] = 'X'
                map[nextGuard.pos.y][nextGuard.pos.x] = dirToChar(nextGuard.direction)
                visitedPositions.add(nextGuard.pos)
            }

            guard = nextGuard

        } while (isPosInMap(guard.pos, map))

        return visitedPositions.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val testInput1 = readInputLines("Day06_test")
    check(part1(testInput1) == 41)

    val input = readInputLines("Day06")
    part1(input).println()

    part2(input).println()
}
