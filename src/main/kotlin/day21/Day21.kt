package day21

import utils.*
import kotlin.math.abs

data class Input(
    val codes: List<String>
) {
    companion object {
        fun parse(name: String) = readInputLines(name)
            .filter(String::isNotBlank)
            .let { Input(it) }
    }
}

interface Key {
    val value: Char
    val pos: Vec2I
}

enum class DirKey(
    override val value: Char,
    override val pos: Vec2I
) : Key {
    BLANK(' ', Vec2I(0, 0)),
    UP('^', Vec2I(1, 0)),
    A('A', Vec2I(2, 0)),
    LEFT('<', Vec2I(0, 1)),
    DOWN('v', Vec2I(1, 1)),
    RIGHT('>', Vec2I(2, 1));

    companion object {
        fun find(char: Char, defValue: DirKey = BLANK) = entries.firstOrNull { it.value == char } ?: defValue

        fun find(from: Vec2I, to: Vec2I): DirKey {
            return when (val dir = to - from) {
                Vec2I(1, 0) -> RIGHT
                Vec2I(0, 1) -> DOWN
                Vec2I(-1, 0) -> LEFT
                Vec2I(0, -1) -> UP
                else -> throw AssertionError("invalid direction $dir")
            }
        }
    }
}

enum class NumKey(
    override val value: Char,
    override val pos: Vec2I
) : Key {
    BLANK(' ', Vec2I(0, 3)),
    ZERO('0', Vec2I(1, 3)),
    A('A', Vec2I(2, 3)),
    ONE('1', Vec2I(0, 2)),
    TWO('2', Vec2I(1, 2)),
    THREE('3', Vec2I(2, 2)),
    FOUR('4', Vec2I(0, 1)),
    FIVE('5', Vec2I(1, 1)),
    SIX('6', Vec2I(2, 1)),
    SEVEN('7', Vec2I(0, 0)),
    EIGHT('8', Vec2I(1, 0)),
    NINE('9', Vec2I(2, 0));

    companion object {
        fun find(char: Char, defValue: NumKey = BLANK) =
            NumKey.entries.firstOrNull { it.value == char } ?: defValue
    }
}

typealias DirMove = Edge<DirKey>
typealias NumMove = Edge<NumKey>

fun Char.toNumKey() = NumKey.find(this)
fun Char.toDirKey() = DirKey.find(this)
fun List<DirKey>.toDirString() = this.map(DirKey::value).joinToString("")

data class PadPaths(
    val dirPad: Map<DirMove, Set<String>>,
    val numPad: Map<NumMove, Set<String>>
)

fun <T : Key> generatePath(
    from: T,
    to: T,
    blank: T
): Set<String> {
    val diff = to.pos - from.pos

    val verticalMoves = when {
        0 == diff.y -> listOf()
        0 < diff.y -> List(diff.y) { Vec2I(0, 1) }
        else -> List(abs(diff.y)) { Vec2I(0, -1) }
    }

    val horizontalMoves = when {
        0 == diff.x -> listOf()
        0 < diff.x -> List(diff.x) { Vec2I(1, 0) }
        else -> List(abs(diff.x)) { Vec2I(-1, 0) }
    }

    val variantAh = horizontalMoves.scan(from.pos) { a, v -> a + v }
    val variantAv = verticalMoves.scan(variantAh.last()) { a, v -> a + v }
    val variantA = variantAh + variantAv.drop(1)
    val variantAValid = blank.pos !in variantA

    val variantBv = verticalMoves.scan(from.pos) { a, v -> a + v }
    val variantBh = horizontalMoves.scan(variantBv.last()) { a, v -> a + v }
    val variantB = variantBv + variantBh.drop(1)
    val variantBValid = blank.pos !in variantB

    val result = if (variantAValid && variantBValid) {
        listOf(variantA, variantB)
    } else if (variantAValid) {
        listOf(variantA)
    } else {
        listOf(variantB)
    }

    return result.map { v -> v.windowed(2).map { DirKey.find(it[0], it[1]) }.toDirString() }.toSet()
}

private fun generatePadPaths(): PadPaths {
    val shortestDirKeyMoves = mutableMapOf<DirMove, Set<String>>()
    val validDirKeys = DirKey.entries.filter { DirKey.BLANK != it }
    for (start in validDirKeys) {
        for (end in validDirKeys) {
            val move = DirMove(start, end)

            if (start == end) {
                shortestDirKeyMoves[move] = setOf("")
                continue
            }

            shortestDirKeyMoves[move] = generatePath(start, end, DirKey.BLANK)
        }
    }

    val shortestNumKeyMoves = mutableMapOf<NumMove, Set<String>>()
    val validNumKeys = NumKey.entries.filter { NumKey.BLANK != it }
    for (start in validNumKeys) {
        for (end in validNumKeys) {
            if (start == end) {
                shortestNumKeyMoves[NumMove(start, end)] = setOf("")
                continue
            }

            shortestNumKeyMoves[NumMove(start, end)] = generatePath(start, end, NumKey.BLANK)
        }
    }

    return PadPaths(shortestDirKeyMoves, shortestNumKeyMoves)
}

private fun partX(
    dirRobotCount: Int,
    input: Input,
    padPaths: PadPaths
): Long {
    val cache = mutableMapOf<Int, MutableMap<String, Long>>()
    repeat(dirRobotCount + 1) {
        cache[it] = mutableMapOf()
    }

    var result = 0L
    for (code in input.codes) {
        val codePath = "A$code"
        var total = 0L
        codePath.toList().windowed(2) { wnd ->
            val move = NumMove(wnd[0].toNumKey(), wnd[1].toNumKey())
            val paths = padPaths.numPad[move] ?: listOf()
            if (paths.isNotEmpty()) {
                total += paths.minOf { findShortestPath(it, padPaths, cache, dirRobotCount) }
            }
        }

        result += total * code.substringBefore('A').toInt()
    }
    return result
}

fun findShortestPath(
    code: String,
    padPaths: PadPaths,
    cache: MutableMap<Int, MutableMap<String, Long>>,
    depth: Int
): Long {
    if (0 == depth) {
        return code.length + 1L
    }

    val depthCache = cache[depth]!!
    if (code in depthCache) {
        return depthCache[code]!!
    }

    val codePath = "A${code}A"
    var total = 0L
    codePath.toList().windowed(2) { wnd ->
        val move = DirMove(wnd[0].toDirKey(), wnd[1].toDirKey())
        val paths = padPaths.dirPad[move] ?: listOf()
        if (paths.isNotEmpty()) {
            total += paths.minOf { findShortestPath(it, padPaths, cache, depth - 1) }
        }
    }

    cache.getOrPut(depth) { mutableMapOf() }[code] = total
    return total
}

private fun part1(
    input: Input,
    shortestPaths: PadPaths
): Long {
    val dirRobotCount = 2
    return partX(dirRobotCount, input, shortestPaths)
}

private fun part2(
    input: Input,
    shortestPaths: PadPaths
): Long {
    val dirRobotCount = 25
    return partX(dirRobotCount, input, shortestPaths)
}

fun main() {
    val shortestPaths = generatePadPaths()
    part1(Input.parse("day21/part1_test1"), shortestPaths).checkResult(126384)
    part1(Input.parse("day21/Day21"), shortestPaths).println("Part1 input")
    part2(Input.parse("day21/Day21"), shortestPaths).println("Part2 input")
}
