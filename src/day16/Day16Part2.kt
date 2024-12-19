package day16

import utils.*
import java.util.*

enum class MazeTile(
    val char: Char
) {
    EMPTY('.'),
    WALL('#')
}

typealias ReindeerMaze = Maze<MazeTile>

data class Input(
    val map: MutableList<MutableList<MazeTile>>,
    val start: Vec2I,
    val end: Vec2I
) {
    companion object {
        fun parse(name: String): Input {
            val lines = readInputLines(name)

            val map = mutableListOf<MutableList<MazeTile>>()
            var start = Vec2I(-1, -1)
            var end = Vec2I(-1, -1)
            for (y in lines.indices) {
                val line = lines[y]
                if (line.isBlank()) continue


                val row = mutableListOf<MazeTile>()
                map.add(row)
                for (x in line.indices) {
                    val char = line[x]
                    if ('S' == char) {
                        start = Vec2I(x, y)
                        row.add(MazeTile.EMPTY)
                    } else if ('E' == char) {
                        end = Vec2I(x, y)
                        row.add(MazeTile.EMPTY)
                    } else if ('#' == char) {
                        row.add(MazeTile.WALL)
                    } else {
                        row.add(MazeTile.EMPTY)
                    }
                }
            }

            return Input(map = map, start = start, end = end)
        }
    }
}

data class State(
    val pos: Vec2I,
    val dir: Direction,
    val score: Int,
    val path: List<Vec2I> = listOf()
)

fun findBestSeats(
    maze: ReindeerMaze,
    startPos: Vec2I,
    endPos: Vec2I
): List<Vec2I> {
    val bestSeats = mutableSetOf<Vec2I>()
    val scores = mutableMapOf<Pair<Vec2I, Direction>, Int>()

    val queue = PriorityQueue<State>(compareBy { it.score })
    queue.add(State(startPos, Direction.EAST, 0, listOf()))

    var lowestScore = Int.MAX_VALUE
    while (queue.isNotEmpty()) {
        val current = queue.poll()
        if (current.score > scores.getOrDefault(current.pos to current.dir, Int.MAX_VALUE)) {
            continue
        }

        scores[current.pos to current.dir] = current.score
        if (current.pos == endPos) {
            if (current.score > lowestScore) {
                break
            }

            lowestScore = current.score
            bestSeats.addAll(current.path + endPos)
        }

        val nextMoves = listOf(
            current.dir to 1,
            current.dir.turnLeft() to 1001,
            current.dir.turnRight() to 1001
        )

        for ((dir, score) in nextMoves) {
            val nestPos = current.pos + dir
            if (MazeTile.WALL == maze[nestPos]) {
                continue
            }

            queue.add(State(nestPos, dir, current.score + score, current.path + current.pos))
        }
    }

    return bestSeats.toList()
}

fun main() {
    fun part2(input: Input): Int {
        val maze = ReindeerMaze(input.map)

        val bestSeats = findBestSeats(maze, input.start, input.end)
        maze.renderAsString(MazeTile::char, listOf(bestSeats to 'O')).println()

        return bestSeats.size
    }

    part2(Input.parse("day16/part1_test1")).also { it.println("Part1 Test1") }.checkResult(45)
    part2(Input.parse("day16/part1_test2")).also { it.println("Part1 Test2") }.checkResult(64)
    part2(Input.parse("day16/Day16")).also { it.println("Part1") }
}

