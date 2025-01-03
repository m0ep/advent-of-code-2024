package day08

import Vec2I
import checkResult
import printHeader
import println
import readInputLines

sealed class Tile {
    object Empty : Tile()
    data class Antenna(val frequency: Char) : Tile()


    fun toChar(): Char = when (this) {
        Empty -> '.'
        is Antenna -> frequency
    }

    override fun toString(): String = toChar().toString()

    companion object {
        fun fromChar(char: Char): Tile {
            return if (char.isDigit() || char.isLetter()) {
                Antenna(char)
            } else {
                Empty
            }
        }
    }
}

data class TileMap(
    val tiles: List<List<Tile>>
) {
    val width = tiles.first().size
    val height = tiles.size

    fun withPosition(): Sequence<Pair<Vec2I, Tile>> {
        return tiles.asSequence()
            .withIndex()
            .flatMap { (y, line) ->
                line.withIndex().map { (x, tile) ->
                    Vec2I(x, y) to tile
                }
            }
    }

    fun isPositionOnMap(pos: Vec2I) = pos.y in 0..<height && pos.x in 0..<width

    override fun toString(): String {
        return tiles.joinToString("\n") {
            it.map(Tile::toChar).joinToString("")
        }
    }

    companion object {
        fun fromInput(input: List<String>): TileMap {
            return input.map { it.toList() }
                .map { it.toList().map { char -> Tile.fromChar(char) } }
                .let { TileMap(it) }
        }
    }
}

private fun <T> permutation(
    first: List<T>,
): List<Pair<T, T>> {
    val result = mutableListOf<Pair<T, T>>()
    for (a in first) {
        for (b in first) {
            result.add(a to b)
        }
    }

    return result
}

private fun printPositions(
    positions: List<Vec2I>,
    width: Int,
    height: Int,
) {
    val map = MutableList(height) { MutableList(width) { '.' } }
    positions.forEach { map[it.y][it.x] = '#' }

    for (y in 0 until height) {
        for (x in 0 until width) {
            print(map[y][x])
        }
        println()
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val tileMap = TileMap.fromInput(input)

        val antennaPosByFreq = tileMap.withPosition()
            .filter { (_, tile) -> tile is Tile.Antenna }
            .groupBy({ (it.second as Tile.Antenna).frequency }, { it.first })

        val harmonics = antennaPosByFreq.flatMap { (_, v) ->
            permutation(v)
                .filter { it.first != it.second }
                .map { it.second + (it.second - it.first) }
                .filter { tileMap.isPositionOnMap(it) }
        }.distinct()

        printPositions(harmonics, tileMap.width, tileMap.height)
        return harmonics.count()
    }

    fun part2(input: List<String>): Int {
        val tileMap = TileMap.fromInput(input)

        val antennaPosByFreq = tileMap.withPosition()
            .filter { (_, tile) -> tile is Tile.Antenna }
            .groupBy({ (it.second as Tile.Antenna).frequency }, { it.first })

        val antennas = antennaPosByFreq.values.flatten()
        val harmonics = antennaPosByFreq.flatMap { (_, v) ->
            permutation(v)
                .filter { it.first != it.second }
                .flatMap { (a, b) ->
                    val dist = (b - a)
                    generateSequence(1) { it + 1 }
                        .map { b + (dist * it) }
                        .takeWhile { tileMap.isPositionOnMap(it) }
                }.filter { it !in antennas }
        }.distinct()

        printPositions(harmonics, tileMap.width, tileMap.height)
        return harmonics.count() + antennas.size
    }

    "part1 test1".printHeader()
    val testInput = readInputLines("day08/Day08_test")
    part1(testInput).checkResult(14)

    "part1 verify".printHeader()
    val input = readInputLines("day08/Day08")
    part1(input).println("part1")

    "part2 test1".printHeader()
    val testInput3 = readInputLines("day08/Day08_test3")
    part2(testInput3).checkResult(9)

    "part2 test2".printHeader()
    val testInput2 = readInputLines("day08/Day08_test2")
    part2(testInput2).checkResult(34)

    "part2 verify".printHeader()
    part2(input).println("part2")
}
