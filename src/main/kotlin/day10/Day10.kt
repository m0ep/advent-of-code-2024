package day10

import utils.Vec2I
import utils.checkResult
import utils.printHeader
import utils.println
import utils.readInput2DMapChar

data class TopoMap(
    val map: List<List<Int>>
) {
    val height: Int
        get() = map.size

    val width: Int
        get() = map[0].size

    fun getHeight(
        pos: Vec2I
    ): Int {

        return map[pos.y][pos.x]
    }

    fun isValidPos(
        pos: Vec2I
    ): Boolean {
        if (0 > pos.y || pos.y >= height) return false
        if (0 > pos.x || pos.x >= width) return false
        if (-1 == getHeight(pos)) return false
        return true
    }
}

data class VisitMap(
    val width: Int,
    val height: Int
) {
    val map = MutableList(height) { MutableList(width) { 0 } }

    fun get(
        pos: Vec2I
    ): Int {
        return map[pos.y][pos.x]
    }

    fun visit(pos: Vec2I) {
        map[pos.y][pos.x]++
    }
}

private fun readMap(name: String): TopoMap =
    readInput2DMapChar(name)
        .map { line ->
            line.map {
                if ('.' == it) -1 else it.digitToInt()
            }
        }.let { TopoMap(it) }

private fun findPathToPeak(
    map: TopoMap,
    pos: Vec2I
): Set<Vec2I> {
    val currentHeight = map.getHeight(pos)

    val result = mutableSetOf<Vec2I>()

    val north = pos + Vec2I(0, -1)
    if (map.isValidPos(north)) {
        val nextHeight = map.getHeight(north)
        if(1 == nextHeight - currentHeight){
            if(9 == nextHeight){
                result.add(north)
            } else{
                result.addAll(findPathToPeak(map, north))
            }
        }
    }

    val east = pos + Vec2I(1, 0)
    if (map.isValidPos(east)) {
        val nextHeight = map.getHeight(east)
        if(1 == nextHeight - currentHeight){
            if(9 == nextHeight){
                result.add(east)
            } else{
                result.addAll(findPathToPeak(map, east))
            }
        }
    }

    val south = pos + Vec2I(0, 1)
    if (map.isValidPos(south)) {
        val nextHeight = map.getHeight(south)
        if(1 == nextHeight - currentHeight){
            if(9 == nextHeight){
                result.add(south)
            } else{
                result.addAll(findPathToPeak(map, south))
            }
        }
    }

    val west = pos + Vec2I(-1, 0)
    if (map.isValidPos(west)) {
        val nextHeight = map.getHeight(west)
        if(1 == nextHeight - currentHeight){
            if(9 == nextHeight){
                result.add(west)
            } else{
                result.addAll(findPathToPeak(map, west))
            }
        }
    }

    return result
}

fun findPathToTrailHead(
    topoMap: TopoMap,
    visitMap: VisitMap,
    pos: Vec2I
) {
    visitMap.visit(pos)
    val currentHeight = topoMap.getHeight(pos)

    val north = pos + Vec2I(0, -1)
    if (topoMap.isValidPos(north)) {
        val nextHeight = topoMap.getHeight(north)
        if (1 == currentHeight - nextHeight) {
            findPathToTrailHead(topoMap, visitMap, north)
        }
    }

    val east = pos + Vec2I(1, 0)
    if (topoMap.isValidPos(east)) {
        val nextHeight = topoMap.getHeight(east)
        if (1 == currentHeight - nextHeight) {
            findPathToTrailHead(topoMap, visitMap, east)
        }
    }

    val south = pos + Vec2I(0, 1)
    if (topoMap.isValidPos(south)) {
        val nextHeight = topoMap.getHeight(south)
        if (1 == currentHeight - nextHeight) {
            findPathToTrailHead(topoMap, visitMap, south)
        }
    }

    val west = pos + Vec2I(-1, 0)
    if (topoMap.isValidPos(west)) {
        val nextHeight = topoMap.getHeight(west)
        if (1 == currentHeight - nextHeight) {
            findPathToTrailHead(topoMap, visitMap, west)
        }
    }
}

fun main() {
    fun part1(
        topoMap: TopoMap
    ): Int {

        val startPositions = topoMap.map.flatMapIndexed { y, l ->
            l.withIndex()
                .filter { v -> 0 == v.value }
                .map { Vec2I(it.index, y) }
        }

        return startPositions
            .map { findPathToPeak(topoMap, it) }
            .sumOf { it.size }
    }

    fun part2(
        topoMap: TopoMap
    ): Int {
        val trailHeads = mutableSetOf<Vec2I>()
        val trailPeaks = mutableSetOf<Vec2I>()
        for (y in 0 until topoMap.height) {
            for (x in 0 until topoMap.width) {
                val position = Vec2I(x, y)
                val height = topoMap.getHeight(position)
                if (0 == height) {
                    trailHeads.add(position)
                } else if (9 == height) {
                    trailPeaks.add(position)
                }
            }
        }

        val visitMap = VisitMap(topoMap.width, topoMap.height)
        trailPeaks.forEach {
            findPathToTrailHead(topoMap, visitMap, it)
        }

        return trailHeads.sumOf { visitMap.get(it) }
    }

    "Part1 - mini test".printHeader()
    val miniTestInput = readMap("day10/part1_test1")
    part1(miniTestInput).also(::println).checkResult(1)

    "Part1 - large test".printHeader()
    val largeTestInput = readMap("day10/part1_test2")
    part1(largeTestInput).also(::println).checkResult(36)

    "Part1 - puzzle input".printHeader()
    val input = readMap("day10/Day10")
    part1(input).println()

    "Part2 - test 1".printHeader()
    val part2Test1Input = readMap("day10/part2_test1")
    part2(part2Test1Input).also(::println).checkResult(3)

    "Part2 - test 2".printHeader()
    val part2Test2Input = readMap("day10/part2_test2")
    part2(part2Test2Input).also(::println).checkResult(13)

    "Part2 - test 3".printHeader()
    val part2Test3Input = readMap("day10/part2_test3")
    part2(part2Test3Input).also(::println).checkResult(227)

    "Part2 - test 4".printHeader()
    val part2Test4Input = readMap("day10/part2_test4")
    part2(part2Test4Input).also(::println).checkResult(81)

    "Part2 - puzzle input".printHeader()
    part2(input).also { println(it) }
}
