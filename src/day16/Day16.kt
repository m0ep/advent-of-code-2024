package day16

import Direction
import Vec2
import checkResult
import println
import readInput2DMapChar

private data class Node2D(
    var pos: Vec2,
    var neighbors: MutableMap<Direction, Node2D> = mutableMapOf()
) {
    operator fun get(dir: Direction): Node2D? = neighbors[dir]
    operator fun set(dir: Direction, node: Node2D) {
        neighbors[dir] = node
    }


    override fun toString(): String {
        val sb = StringBuilder()

        sb.append("{Node2D(${pos.x},${pos.y})|")

        for (dir in Direction.entries) {
            val node = neighbors[dir]
            if (null != node) {
                sb.append("${dir.name.substring(0, 1)}(${node.pos.x},${node.pos.y})")
            }
        }

        return sb.append("}").toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node2D

        return pos == other.pos
    }

    override fun hashCode(): Int {
        return pos.hashCode()
    }
}

private data class Maze(
    val width: Int,
    val height: Int,
    val nodes: Map<Vec2, Node2D>,
    val start: Node2D,
    val end: Node2D
) {
    fun to2DMapString(): String {
        val sb = StringBuilder()
        for (y in 0..<height) {
            for (x in 0..<width) {
                val pos = Vec2(x, y)
                if (pos in nodes) {
                    if (pos == start.pos) {
                        sb.append("S")
                    } else if (pos == end.pos) {
                        sb.append("E")
                    } else {
                        sb.append(".")
                    }
                } else {
                    sb.append('#')
                }
            }

            sb.append("\n")
        }

        return sb.toString().trimEnd()
    }

    fun to2DMapStringWithPath(
        path: List<Pair<Node2D, Direction>>
    ): String {

        val dirMap = path.associate { it.first.pos to it.second }

        val sb = StringBuilder()
        for (y in 0..<height) {
            for (x in 0..<width) {
                val pos = Vec2(x, y)
                if (pos in nodes) {
                    when (pos) {
                        in dirMap -> sb.append(dirMap[pos]!!.toChar())
                        else -> sb.append(".")
                    }
                } else {
                    sb.append('#')
                }
            }

            sb.append("\n")
        }

        return sb.toString().trimEnd()
    }

    fun to2DMapStringWithNodes(
        nodesToDisplay: Set<Node2D>
    ): String {

        val sb = StringBuilder()
        for (y in 0..<height) {
            for (x in 0..<width) {
                val pos = Vec2(x, y)
                if (pos in nodes) {
                    if (nodes[pos] in nodesToDisplay) {
                        sb.append('O')
                    } else {
                        sb.append('.')
                    }
                } else {
                    sb.append('#')
                }
            }

            sb.append("\n")
        }

        return sb.toString().trimEnd()
    }
}

private fun parseMaze(name: String): Maze {
    val map = readInput2DMapChar(name)

    var startNode: Node2D? = null
    var endNode: Node2D? = null
    val posToNode = mutableMapOf<Vec2, Node2D>()
    for (y in map.indices) {
        for (x in map[y].indices) {
            val pos = Vec2(x, y)
            val char = map[y][x]
            if ('#' == char) continue

            val node = posToNode.getOrPut(pos) { Node2D(pos) }
            if ('S' == char) {
                startNode = node
            } else if ('E' == char) {
                endNode = node
            }

            for (direction in Direction.entries) {
                val nextPos = pos + direction
                val nextChar = map[nextPos.y][nextPos.x]
                if ('#' == nextChar) continue

                if (nextPos in posToNode) {
                    node[direction] = posToNode[nextPos]!!
                } else {
                    val nextNode = Node2D(nextPos)
                    node[direction] = nextNode
                    posToNode[nextPos] = nextNode
                }
            }
        }
    }

    assert(null != startNode)
    assert(null != endNode)
    return Maze(
        map[0].size,
        map.size,
        posToNode,
        startNode!!,
        endNode!!
    )
}

private fun calcCosts(
    srcDir: Direction,
    trgDir: Direction
): Int {
    return if (trgDir == srcDir) 1 else 1001
}

private fun findShortestPathPart1(
    maze: Maze
): List<Pair<Node2D, Direction>> {
    val start = maze.start
    val end = maze.end

    val h = { n: Node2D -> n.pos.distSqrt(end.pos) }

    val openSet = mutableSetOf(start)
    val closedSet = mutableSetOf<Node2D>()
    val cameFrom = mutableMapOf<Node2D, Node2D>()
    val dirFrom = mutableMapOf(start to Direction.EAST)

    val gScore = mutableMapOf<Node2D, Int>()
    gScore[start] = 0

    val fScore = mutableMapOf<Node2D, Int>()
    fScore[start] = h(start)

    while (openSet.isNotEmpty()) {
        val current = openSet.minBy { fScore.getOrDefault(it, Int.MAX_VALUE) }
        if (current.pos == end.pos) {
            return reconstructPath(cameFrom, dirFrom, current)
        }

        val currentDir = dirFrom[current]!!

        openSet.remove(current)
        closedSet.add(current)
        for ((neighborDir, neighbor) in current.neighbors) {
            if (neighbor in closedSet) continue

            val costs = calcCosts(currentDir, neighborDir)
            val tg = gScore.getOrDefault(current, Int.MAX_VALUE) + costs
            if (tg < gScore.getOrDefault(neighbor, Int.MAX_VALUE)) {
                cameFrom[neighbor] = current
                dirFrom[neighbor] = neighborDir

                gScore[neighbor] = tg
                fScore[neighbor] = tg + h(neighbor)
                if (neighbor !in openSet) {
                    openSet.add(neighbor)
                }
            }
        }
    }

    throw AssertionError("No path to end")
}

private fun reconstructPath(
    cameFrom: Map<Node2D, Node2D>,
    dirFrom: Map<Node2D, Direction>,
    end: Node2D
): List<Pair<Node2D, Direction>> {
    var currentNode = end
    val path = ArrayDeque(listOf(end to dirFrom[end]!!))

    while (currentNode in cameFrom) {
        currentNode = cameFrom[currentNode]!!
        val currentDir = dirFrom[currentNode]!!
        path.addFirst(currentNode to currentDir)
    }

    return path
}

private fun findShortestPathsPart2(
    maze: Maze
): Set<Node2D> {
    val start = maze.start
    val end = maze.end

    val h = { n: Node2D -> n.pos.distSqrt(end.pos) }

    val openSet = ArrayDeque(listOf(start to Direction.WEST))
    val closedSet = mutableSetOf<Pair<Node2D, Direction>>()

    val cameFrom = mutableMapOf<Node2D, Node2D>()

    val costsFrom = mutableMapOf<Node2D, Pair<Int, MutableSet<Node2D>>>()

    val gScore = mutableMapOf<Node2D, Int>()
    gScore[start] = 0

    while (openSet.isNotEmpty()) {
        val current = openSet.removeFirst()
        val (currentNode, currentDir) = current
        if (current in closedSet) continue

        closedSet.add(current)
        for (neighbor in currentNode.neighbors) {
            val (neighborDir, neighborNode) = neighbor
            val c = calcCosts(currentDir, neighborDir)
            val tg = gScore.getOrDefault(currentNode, Int.MAX_VALUE) + c

            //costsTo.getOrPut(currentNode){ mutableMapOf() }[neighborNode] = tg
            //costsFrom.getOrPut(neighborNode){ mutableMapOf() }[currentNode] = gScore.getOrDefault(currentNode, Int.MAX_VALUE)

            val prevScore = gScore.getOrDefault(neighborNode, Int.MAX_VALUE)
            if (tg < prevScore) {
                costsFrom[neighborNode] = tg to  mutableSetOf(currentNode)

                cameFrom[neighborNode] = currentNode
                gScore[neighborNode] = tg
                if (neighborNode to neighborDir !in openSet) {
                    openSet.add(neighborNode to neighborDir)
                }
            } else if(tg == prevScore){
                costsFrom.getOrPut(neighborNode){tg to mutableSetOf() }.second.add(currentNode)
            }
        }
    }


    val minPathNodes = mutableSetOf<Node2D>()
    val queue = ArrayDeque(listOf(end))

    while(queue.isNotEmpty()){
        val current = queue.removeFirst()
        if(current in minPathNodes) continue

        minPathNodes.add(current)
        if(current in cameFrom) {
            queue.addAll(costsFrom[current]!!.second)
        }
    }

    return minPathNodes
}

fun main() {
    fun part1(input: Maze): Int {
        input.to2DMapString().println()
        input.println()

        val result = findShortestPathPart1(input)

        var lastDir = result[0].second
        var costs = 0
        for (i in 1..<result.size) {
            val dir = result[i].second
            costs += calcCosts(lastDir, dir)
            lastDir = dir
        }


        input.to2DMapStringWithPath(
            result
        ).println()

        return costs.also { it.println() }
    }

    fun part2(input: Maze): Int {
        input.to2DMapString().println()
        input.println()

        val result = findShortestPathsPart2(input)
        input.to2DMapStringWithNodes(result).println()

        return result.size
    }

    //part1(parseMaze("day16/part1_test1")).checkResult(7036)
    //part1(parseMaze("day16/part1_test2")).checkResult(11048)

    //part1(parseMaze("day16/Day16")).println()


    part2(parseMaze("day16/part2_test1")).checkResult(10)
    part2(parseMaze("day16/part1_test1")).checkResult(45)
    //part2(parseMaze("day16/part1_test2")).checkResult(64)
    //part2(input).println()
}
