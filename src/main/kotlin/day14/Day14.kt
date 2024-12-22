package day14

import utils.Vec2I
import utils.checkResult
import utils.copyOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import utils.println
import utils.readInputLines
import kotlin.math.max
import kotlin.time.measureTimedValue

private data class Robot(
    val pos: Vec2I,
    val vel: Vec2I
)

private fun parseRobotInput(
    value: String
): Robot {
    val regex = "^p=([+-]?\\d+),([+-]?\\d+) v=([+-]?\\d+),([+-]?\\d+)$".toRegex()
    val matchResult = regex.find(value) ?: throw AssertionError()

    val (px, py, vx, vy) = matchResult.destructured

    return Robot(
        Vec2I(px.toInt(), py.toInt()),
        Vec2I(vx.toInt(), vy.toInt())
    )
}

private fun calcMapSize(
    robots: List<Robot>
): Vec2I {
    return robots.fold(Vec2I(0, 0)) { a, r ->
        Vec2I(max(a.x, r.pos.x), max(a.y, r.pos.y))
    }.let { it + Vec2I(1, 1) }
}

private fun simulateRobot(
    robot: Robot,
    mapSize: Vec2I
): Robot {
    val fixPos: (Int, Int) -> Int = { p, w ->
        if (0 > p) {
            p + w
        } else if (p >= w) {
            p - w
        } else {
            p
        }
    }

    val nextPos = robot.pos + robot.vel



    return Robot(
        Vec2I(
            fixPos(nextPos.x, mapSize.x),
            fixPos(nextPos.y, mapSize.y)
        ), robot.vel
    )
}

private fun simulateSeconds(
    robots: List<Robot>,
    mapSize: Vec2I,
    seconds: Int
): List<Robot> = runBlocking {
    val result = mutableListOf<Robot>()
    val writeMutex = Mutex()
    for (robot in robots) {
        launch {
            (0..<seconds).fold(robot) { r, _ ->
                simulateRobot(r, mapSize)
            }.let { writeMutex.withLock { result.add(it) } }
        }
    }

    return@runBlocking result
}

private fun printMap(
    robots: List<Robot>,
    mapSize: Vec2I
) {
    val map = MutableList(mapSize.y) { MutableList(mapSize.x) { 0 } }
    robots.forEach { r -> map[r.pos.y][r.pos.x]++ }

    for (y in 0..<map.size) {
        for (x in 0..<map[y].size) {
            val v = map[y][x]
            print((if (0 == v) '.' else v.toString()))
        }
        println()
    }
}

private data class Quadrant(
    val min: Vec2I,
    val max: Vec2I
) {
    fun contains(pos: Vec2I): Boolean = pos.x in min.x..max.x && pos.y in min.y..max.y
}

private fun calcQuadrants(
    mapSize: Vec2I,
): List<Quadrant> {
    val w = mapSize.x
    val hw = w / 2
    val h = mapSize.y
    val hh = h / 2

    return (0..3).map { quadrant ->
        val min = when (quadrant) {
            0 -> Vec2I(0, 0)
            1 -> Vec2I(w - hw, 0)
            2 -> Vec2I(w - hw, h - hh)
            3 -> Vec2I(0, h - hh)
            else -> throw AssertionError()
        }

        val max = when (quadrant) {
            0 -> Vec2I(hw - 1, hh - 1)
            1 -> Vec2I(w - 1, hh - 1)
            2 -> Vec2I(w - 1, h - 1)
            3 -> Vec2I(hw - 1, h - 1)
            else -> throw AssertionError()
        }

        Quadrant(min, max)
    }
}

private fun findNumDirectNeighbours(
    robots: List<Robot>
): List<Int>  {
    val result = mutableListOf<Int>()

    for (robot in robots) {
        robots.filter { robot.pos.isNeighbor(it.pos) }
            .distinctBy { it.pos }
            .count()
            .let { result.add(it) }
    }

    return result
}

fun main() {
    fun part1(
        input: List<Robot>,
        mapSize: Vec2I
    ): Int {
        val simulationRes = simulateSeconds(input, mapSize, 100)
        printMap(simulationRes, mapSize)

        val quadrants = calcQuadrants(mapSize)
        val quadrantCount = MutableList(4) { 0 }
        for (robot in simulationRes) {
            for (q in 0..3) {
                if (quadrants[q].contains(robot.pos)) {
                    quadrantCount[q]++
                }
            }
        }

        return quadrantCount.fold(1) { a, q -> a * q }
    }

    fun part2(
        input: List<Robot>,
        mapSize: Vec2I
    ): Int {
        var lastSimulationResult = input.copyOf()
        var numDirectNeighboursMax = Int.MIN_VALUE
        var numSimulatedSec = -1

        (1..10000).forEach { sec ->
            runBlocking {
                val simulationResult = simulateSeconds(lastSimulationResult, mapSize, 1)
                val num = findNumDirectNeighbours(simulationResult).sum()
                if (numDirectNeighboursMax < num) {
                    numDirectNeighboursMax = num
                    numSimulatedSec = sec
                }

                lastSimulationResult = simulationResult
            }
        }

        val result = simulateSeconds(input, mapSize, numSimulatedSec)
        printMap(result, mapSize)

        return numSimulatedSec
    }

    parseRobotInput("p=0,4 v=3,-3").checkResult(Robot(Vec2I(0, 4), Vec2I(3, -3)))

    val robot = parseRobotInput("p=2,4 v=2,-3")
    simulateSeconds(listOf(robot), Vec2I(11, 7), 5)
        .checkResult(listOf(Robot(Vec2I(1, 3), Vec2I(2, -3))))

    val part1Test1Robots = readInputLines("day14/part1_test1").map(::parseRobotInput)
    val part1Test1MapSize = part1Test1Robots.let(::calcMapSize)
    part1(part1Test1Robots, part1Test1MapSize).checkResult(12)

    val inputRobots = readInputLines("day14/Day14").map(::parseRobotInput)
    val inputMapSize = inputRobots.let(::calcMapSize)

    measureTimedValue {part1(inputRobots, inputMapSize)}.println("Part1")
    measureTimedValue {part2(inputRobots, inputMapSize)}.println("Part2")
}
