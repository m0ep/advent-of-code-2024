package day12

import Vec2I
import println
import readInput2DMapChar
import kotlin.math.sign
import kotlin.time.measureTimedValue

typealias Plant = Char

fun genAdjacent(pos: Vec2I): List<Vec2I> = listOf(
    pos + Vec2I(0, -1), // north
    pos + Vec2I(1, 0), // east
    pos + Vec2I(0, 1), // south
    pos + Vec2I(-1, 0) // west
)

private data class Farm(
    val plotMap: List<List<Plant>>
) {
    val width: Int
        get() = plotMap[0].size
    val height: Int
        get() = plotMap.size

    fun get(pos: Vec2I): Plant = plotMap[pos.y][pos.x]
    fun isInside(pos: Vec2I) = pos.x in 0..<width && pos.y in 0..<height
}

private data class Plot(
    val pos: Vec2I,
    val borders: List<Boolean>
) {
    val hasBorders: Boolean = borders.any { it }
}

private data class Region(
    val type: Plant,
    val plots: Set<Plot>
) {
    val area: Int
        get() = plots.size

    val perimeter: Int
        get() = plots.sumOf { p ->
            p.borders.count { b -> b }
        }
}

private data class Edge(
    val from: Vec2I,
    val to: Vec2I
) {
    val dir: Vec2I = Vec2I(
        (to.x - from.x).sign,
        (to.y - from.y).sign
    )

    override fun toString(): String {
        return "Edge{(${from.x},${from.y}) -> (${to.x},${to.y})}"
    }
}

private fun calculateRegions(farm: Farm): List<Region> {
    val processedPlog = mutableSetOf<Vec2I>()
    val regions = mutableListOf<Region>()
    for (x in 0..<farm.width) {
        for (y in 0..<farm.height) {
            val pos = Vec2I(x, y)
            if (processedPlog.contains(pos)) continue

            val floodPlant = farm.get(pos)
            val floodQueue = ArrayDeque(listOf(pos))
            val floodPlots = mutableSetOf<Plot>()
            while (floodQueue.isNotEmpty()) {
                val curFloodPos = floodQueue.removeFirst()
                val curFloodPlant = farm.get(curFloodPos)
                if (curFloodPlant != floodPlant) {
                    continue
                }

                processedPlog.add(curFloodPos)

                val adjacent = genAdjacent(curFloodPos)
                adjacent.filter { farm.isInside(it) && !processedPlog.contains(it) }
                    .forEach(floodQueue::addLast)

                val borders = genAdjacent(curFloodPos)
                    .map { !farm.isInside(it) || floodPlant != farm.get(it) }

                floodPlots.add(Plot(curFloodPos, borders))
            }

            processedPlog.addAll(floodPlots.map(Plot::pos))

            val region = Region(floodPlant, floodPlots)
            regions.add(region)
        }
    }

    return regions
}

private fun calcEdges(region: Region): Set<Edge> {
    val edges = mutableSetOf<Edge>()
    for (plot in region.plots) {
        if (plot.borders[0]) {
            edges.add(
                Edge(
                    plot.pos,
                    plot.pos + Vec2I(1, 0)
                )
            )
        }

        if (plot.borders[1]) {
            edges.add(
                Edge(
                    plot.pos + Vec2I(1, 0),
                    plot.pos + Vec2I(1, 1)
                )
            )
        }

        if (plot.borders[2]) {
            edges.add(
                Edge(
                    plot.pos + Vec2I(1, 1),
                    plot.pos + Vec2I(0, 1)
                )
            )
        }

        if (plot.borders[3]) {
            edges.add(
                Edge(
                    plot.pos + Vec2I(0, 1),
                    plot.pos
                )
            )
        }
    }

    return edges
}

fun main() {
    fun part1(regions: List<Region>): Int {
        return regions.sumOf { it.area * it.perimeter }
    }

    fun calcSides(region: Region): Set<Edge> {
        val edges = calcEdges(region)
        "1: ${region.type} - $edges".println()

        val ignoreEdges = mutableSetOf<Edge>()
        val collapsedEdges = mutableSetOf<Edge>()
        for (edge in edges) {
            if(edge in ignoreEdges){
                continue
            }

            var collapsedEdge = edge

            var next = Edge(edge.to, edge.to + edge.dir)
            while(next in edges){
                collapsedEdge = Edge(collapsedEdge.from, next.to)
                ignoreEdges.add(next)
                next = Edge(next.from + edge.dir, next.to + edge.dir)
            }

            var prev = Edge(edge.from - edge.dir, edge.from)
            while(prev in edges){
                collapsedEdge = Edge(prev.from, collapsedEdge.to)
                ignoreEdges.add(prev)
                prev = Edge(prev.from - edge.dir, prev.to - edge.dir)
            }

            collapsedEdges.add(collapsedEdge)
        }

        "2: ${region.type} - $collapsedEdges".println()
        return collapsedEdges
    }

    fun part2(regions: List<Region>): Int {
        return regions.sumOf { calcSides(it).size * it.area }
    }

    //part1(calculateRegions(parseFarmMap("day12/part1_test1"))).checkResult(140)
    //part1(calculateRegions(parseFarmMap("day12/part1_test2"))).checkResult(772)
    //part1(calculateRegions(parseFarmMap("day12/part1_test3"))).checkResult(1930)


    //part2(calculateRegions(parseFarmMap("day12/part2_test1"))).checkResult(80)
    //part2(calculateRegions(parseFarmMap("day12/part1_test2"))).checkResult(436)
    //part2(calculateRegions(parseFarmMap("day12/part2_test2"))).checkResult(236)
    //part2(calculateRegions(parseFarmMap("day12/part2_test3"))).checkResult(368)
    //part2(calculateRegions(parseFarmMap("day12/part1_test3"))).checkResult(1206)

    val input = Farm(readInput2DMapChar("day12/Day12"))
    val regions = calculateRegions(input)

    val (part1Result, part1Duration) = measureTimedValue { part1(regions) }
    "Part 1 - result=$part1Result took $part1Duration".println()

    val (part2Result, part2Duration) = measureTimedValue { part2(regions) }
    "Part 2 - result=$part2Result took $part2Duration".println()
}
