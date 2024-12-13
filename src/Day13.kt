import java.util.*
import kotlin.collections.ArrayDeque

private data class ClawMachine(
    val aBtnMove: Vec2l,
    val bBtnMove: Vec2l,
    val prizePos: Vec2l
)

private fun parseButtonMove(
    line: String
): Vec2l {
    val regex = "^Button ([AB]): X([+-]\\d+), Y([+-]\\d+)$".toRegex()
    val matchRes = regex.find(line) ?: throw AssertionError()
    val (_, x, y) = matchRes.destructured
    return Vec2l(x.toLong(), y.toLong())
}

private fun parsePrizePos(
    line: String
): Vec2l {
    val regex = "^Prize: X=(\\d+), Y=(\\d+)\$".toRegex()
    val matchRes = regex.find(line) ?: throw AssertionError()
    val (x, y) = matchRes.destructured
    return Vec2l(x.toLong(), y.toLong())
}

private fun parseInput(
    name: String
): List<ClawMachine> {

    val input = readInputLines(name)
    val result = mutableListOf<ClawMachine>()

    val inputQueue = ArrayDeque(input);
    while (inputQueue.isNotEmpty()) {
        val aBtnMove = parseButtonMove(inputQueue.removeFirst())
        val bBtnMove = parseButtonMove(inputQueue.removeFirst())
        val prizePos = parsePrizePos(inputQueue.removeFirst())
        result.add(ClawMachine(aBtnMove, bBtnMove, prizePos))

        if (inputQueue.isNotEmpty()) {
            // skip empty line
            inputQueue.removeFirst()
        }
    }
    return result
}

private fun calcMinMoves(
    clawMachine: ClawMachine
): Optional<Vec2l> {
    val (a, b, t) = clawMachine

    // #A = (by * tx - bx * ty) / (ax * by - ay * bx)
    val nominatorA = b.y * t.x - b.x * t.y
    val denominatorA = a.x * b.y - a.y * b.x
    if (solvable(nominatorA, denominatorA)) {
        return Optional.empty()
    }

    // #B = (ay * tx - ax * ty) / (ay * bx - ax * by)
    val nominatroB = a.y * t.x - a.x * t.y
    val denominatorB = a.y * b.x - a.x * b.y
    if (solvable(nominatroB, denominatorB)) {
        return Optional.empty()
    }

    return Optional.of(
        Vec2l(
            (nominatorA / denominatorA),
            (nominatroB / denominatorB)
        )
    )
}

private fun solvable(nominator: Long, denominator: Long) =
    0L == denominator || 0L != nominator % denominator

fun main() {
    fun part1(input: List<ClawMachine>): Long {
        return input.map(::calcMinMoves)
            .filter { it.isPresent }
            .sumOf {
                val moves = it.get()
                moves.x * 3 + moves.y
            }
    }

    fun part2(input: List<ClawMachine>): Long {
        val corr = Vec2l(10000000000000L, 10000000000000L)
        return input
            .map { it.copy(prizePos = (it.prizePos + corr)) }
            .map(::calcMinMoves)
            .filter { it.isPresent }
            .sumOf {
                val moves = it.get()
                moves.x * 3 + moves.y
            }
    }

    parseButtonMove("Button A: X+94, Y+34").checkResult(Vec2l(94, 34))
    parseButtonMove("Button B: X-32, Y+42").checkResult(Vec2l(-32, 42))
    parsePrizePos("Prize: X=18641, Y=10279").checkResult(Vec2l(18641, 10279))
    part1(parseInput("day13/part1_test1")).checkResult(480)

    part1(parseInput("day13/Day13")).println()
    part2(parseInput("day13/Day13")).println()
}
