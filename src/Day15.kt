import java.util.stream.Stream

private enum class TileD14 {
    EMPTY,
    ROBOT,
    CRATE,
    WALL,
    CRATE_LEFT,
    CRATE_RIGHT;

    fun toChar(): Char {
        return when (this) {
            EMPTY -> '.'
            ROBOT -> '@'
            CRATE -> 'O'
            WALL -> '#'
            CRATE_LEFT -> '['
            CRATE_RIGHT -> ']'
        }
    }
}

private data class Warehouse(
    val map: MutableList<MutableList<TileD14>>
) {
    val width: Int
        get() = map[0].size

    val height: Int
        get() = map.size

    operator fun contains(pos: Vec2): Boolean = pos.x in 0..<width && pos.x in 0..<height
    operator fun get(pos: Vec2): TileD14 = map[pos.y][pos.x]
    operator fun set(pos: Vec2, tile: TileD14) {
        map[pos.y][pos.x] = tile
    }

    fun isTile(pos: Vec2, tile: TileD14): Boolean = tile == this[pos]

    fun copyMutable(): Warehouse = Warehouse(map.map { it.mutableCopyOf() }.toMutableList())

    fun toColorString(
        robotPos: Vec2,
        oldPositions: Set<Vec2> = setOf(),
        newPositions: Set<Vec2> = setOf()
    ): String {
        val newColor = "\u001b[32m"
        val robotColor = "\u001b[36m"
        val oldColor = "\u001b[31m"
        val reset = "\u001b[0m"

        val sb = StringBuilder()
        for (y in 0..<height) {
            for (x in 0..<width) {
                val pos = Vec2(x, y)
                val char = this[pos].toChar()
                when (pos) {
                    robotPos -> sb.append(robotColor).append(char).append(reset)
                    in newPositions -> sb.append(newColor).append(char).append(reset)
                    in oldPositions -> sb.append(oldColor).append(char).append(reset)
                    else -> sb.append(char)
                }
            }

            sb.append("\n")
        }
        return sb.toString().trim()
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (row in map) {
            for (tile in row) {
                sb.append(tile.toChar())
            }
            sb.append("\n")
        }

        return sb.toString().trim()
    }

    fun inflate():Warehouse{
        val inflatedMap = mutableListOf<MutableList<TileD14>>()
        for (wRow in map) {
            val inflatedRow = mutableListOf<TileD14>()
            for (tile in wRow) {
                when (tile) {
                    TileD14.EMPTY -> inflatedRow.addAll(listOf(TileD14.EMPTY, TileD14.EMPTY))
                    TileD14.ROBOT -> inflatedRow.addAll(listOf(TileD14.ROBOT, TileD14.EMPTY))
                    TileD14.CRATE -> inflatedRow.addAll(listOf(TileD14.CRATE_LEFT, TileD14.CRATE_RIGHT))
                    TileD14.WALL -> inflatedRow.addAll(listOf(TileD14.WALL, TileD14.WALL))
                    else -> continue
                }
            }

            inflatedMap.add(inflatedRow)
        }

        return Warehouse(inflatedMap)
    }
}

private fun parseMap(
    name: String,
    inflate: Boolean = false
): Pair<Warehouse, List<Direction>> {
    val input = readInputLines(name)

    val map = mutableListOf<MutableList<TileD14>>()
    var rowIdx = 0
    do {
        val mapRow = input[rowIdx].map {
            when (it) {
                '@' -> TileD14.ROBOT
                '#' -> TileD14.WALL
                'O' -> TileD14.CRATE
                '.' -> TileD14.EMPTY
                '[' -> TileD14.CRATE_LEFT
                ']' -> TileD14.CRATE_RIGHT
                else -> throw AssertionError("Invalid tile '$it' in row $rowIdx")
            }
        }
        map.add(mapRow.toMutableList())

        rowIdx++
    } while (input[rowIdx].isNotBlank())
    rowIdx++

    val directions = mutableListOf<Direction>()
    while (rowIdx < input.size) {
        input[rowIdx].map {
            when (it) {
                '^' -> Direction.NORTH
                '>' -> Direction.EAST
                'v' -> Direction.SOUTH
                '<' -> Direction.WEST
                else -> throw AssertionError("Invalid movement '$it' in row $rowIdx")
            }
        }.let(directions::addAll)

        rowIdx++
    }


    val warehouse= Warehouse(map)
    return if(inflate){
        warehouse.inflate() to directions
    } else {
        warehouse to directions
    }
}

private fun findRobotPos(
    warehouse: Warehouse
): Vec2 {
    for (y in 0..<warehouse.height) {
        for (x in 0..<warehouse.width) {
            val pos = Vec2(x, y)
            if (TileD14.ROBOT == warehouse[pos]) {
                return pos
            }
        }
    }

    throw AssertionError("No robot found")
}

private data class Movable(
    val positions: List<Vec2>,
    val tiles: List<TileD14>
) {
    constructor(
        pos: Vec2,
        tile: TileD14
    ) : this(listOf(pos), listOf(tile))

    constructor(
        pos1: Vec2, tile1: TileD14,
        pos2: Vec2, tile2: TileD14
    ) : this(listOf(pos1, pos2), listOf(tile1, tile2))

    fun move(warehouse: Warehouse, direction: Direction): Movable {
        val newPositions = positions.map { it + direction.toVec2() }
        positions.forEach { warehouse[it] = TileD14.EMPTY }
        newPositions.withIndex().forEach { (i, p) -> warehouse[p] = tiles[i] }
        return Movable(newPositions, tiles)
    }

    fun canMove(warehouse: Warehouse, direction: Direction): Boolean =
        positions.map { it + direction.toVec2() }
            .filter { it !in positions }
            .all { warehouse.isTile(it, TileD14.EMPTY) }

    fun isWallInPath(warehouse: Warehouse, direction: Direction): Boolean =
        positions.any { warehouse.isTile(it + direction.toVec2(), TileD14.WALL) }

    fun getCratesInPath(
        warehouse: Warehouse,
        direction: Direction
    ): List<Movable> {
        return positions.stream()
            .flatMap {
                val nextPos = it + direction.toVec2()
                val nextTile = warehouse[nextPos]
                if (TileD14.CRATE_LEFT == nextTile) {
                    Stream.of(
                        Movable(
                            nextPos, TileD14.CRATE_LEFT,
                            nextPos + Vec2(1, 0), TileD14.CRATE_RIGHT
                        )
                    )
                } else if (TileD14.CRATE_RIGHT == nextTile) {
                    Stream.of(
                        Movable(
                            nextPos - Vec2(1, 0), TileD14.CRATE_LEFT,
                            nextPos, TileD14.CRATE_RIGHT
                        )
                    )
                } else {
                    Stream.of()
                }
            }.distinct()
            .toList()
    }
}

fun main() {
    fun part1(
        input: Pair<Warehouse, List<Direction>>)
    : Int {
        val (warehouse, directions) = input

        var robotPos = findRobotPos(warehouse)
        for (direction in directions) {
            //"next dir: ${direction.toChar()}".println()
            val dirVec = direction.toVec2()

            val checkedCrates = mutableSetOf<Vec2>()
            val moveStack = ArrayDeque(listOf(robotPos))
            while (moveStack.isNotEmpty()) {
                val tilePos = moveStack.first()
                val currentTile = warehouse[tilePos]
                val nextPos = tilePos + dirVec
                if (warehouse.isTile(nextPos, TileD14.WALL)) {
                    moveStack.removeFirst()
                } else if (warehouse.isTile(nextPos, TileD14.EMPTY)) {
                    moveStack.removeFirst()
                    warehouse[tilePos] = TileD14.EMPTY
                    warehouse[nextPos] = currentTile
                } else if (warehouse.isTile(nextPos, TileD14.CRATE)) {
                    if (nextPos in checkedCrates) {
                        moveStack.removeFirst()
                    } else {
                        checkedCrates.add(nextPos)
                        moveStack.addFirst(nextPos)
                    }
                }
            }

            //"Dir: ${direction.toChar()}".printHeader()
            //warehouse.println()
            val nextRobotPos = robotPos + dirVec
            if (warehouse.isTile(nextRobotPos, TileD14.ROBOT)) {
                robotPos = nextRobotPos
            }
        }

        var result = 0
        for (y in 0..<warehouse.height) {
            for (x in 0..<warehouse.width) {
                val pos = Vec2(x, y)
                if (TileD14.CRATE == warehouse[pos]) {
                    result += (100 * y + x)
                }
            }
        }

        return result
    }

    fun part2(
        input: Pair<Warehouse, List<Direction>>
    ): Int {
        val (initialWarehouse, directions) = input

        var robotPos = findRobotPos(initialWarehouse)
        "init".printHeader()
        initialWarehouse.toColorString(robotPos).println()

        val directionQueue = ArrayDeque(directions)
        var workingWarehouse = initialWarehouse
        while (directionQueue.isNotEmpty()) {
            val direction = directionQueue.removeFirst()

            val robotMovable = Movable(robotPos, TileD14.ROBOT)
            val checkedCrates = mutableSetOf(robotMovable)
            val moveStack = ArrayDeque(listOf(robotMovable))

            var hitsWall = false
            val moveWarehouse = workingWarehouse.copyMutable()
            while (moveStack.isNotEmpty()) {
                val movable = moveStack.first()
                if (movable.canMove(moveWarehouse, direction)) {
                    movable.move(moveWarehouse, direction)
                    moveStack.removeFirst()
                } else if (movable.isWallInPath(moveWarehouse, direction)) {
                    hitsWall = true
                    break
                } else {
                    val crates = movable.getCratesInPath(moveWarehouse, direction)
                        .filter { !checkedCrates.contains(it) }
                    if (crates.isNotEmpty()) {
                        crates.onEach(checkedCrates::add)
                            .forEach(moveStack::addFirst)
                    } else {
                        moveStack.removeFirst()
                    }
                }
            }

            if(!hitsWall){
                workingWarehouse = moveWarehouse
            }

            val nextRobotPos = robotPos + direction.toVec2()
            if (workingWarehouse.isTile(nextRobotPos, TileD14.ROBOT)) {
                robotPos = nextRobotPos
            }
        }

        "result".printHeader()
        workingWarehouse.toColorString(robotPos).println()

        var result = 0
        for (y in 0..<workingWarehouse.height) {
            for (x in 0..<workingWarehouse.width) {
                val pos = Vec2(x, y)
                if (TileD14.CRATE_LEFT == workingWarehouse[pos]) {
                    result += (100 * y + x)
                }
            }
        }

        return result
    }

    part1(parseMap("day15/part1_test1")).also{it.println("Part1 test1")}.checkResult(2028)
    part1(parseMap("day15/part1_test2")).also{it.println("Part1 test2")}.checkResult(10092)
    part1(parseMap("day15/Day15")).println("Part1 input")

    part2(parseMap("day15/part2_test1", true)).also{it.println("Part2 test1")}.checkResult(618)
    part2(parseMap("day15/part2_test2", false)).println("Part2 test2")
    part2(parseMap("day15/part2_test3", false)).println("Part2 test3")
    part2(parseMap("day15/part1_test2", true)).checkResult(9021)
    part2(parseMap("day15/Day15", true)).println("Part2 input")
}//
