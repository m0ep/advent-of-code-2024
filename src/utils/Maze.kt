package utils

class Maze<T>(
    val data: MutableList<MutableList<T>>
) {
    constructor(w: Int, h: Int, value: T) : this(MutableList(h) { MutableList(w){value} })

    val width: Int
        get() = data[0].size

    val height: Int
        get() = data.size

    // Operators
    operator fun contains(vec2: Vec2I) = vec2.x in 0..<width && vec2.y in 0..<height
    operator fun get(vec2: Vec2I) = data[vec2.y][vec2.x]
    operator fun set(vec2: Vec2I, value: T) {
        data[vec2.y][vec2.x] = value
    }

    // helper methods
    fun fill(value: T){
        for (y in data.indices) {
            for(x in data[y].indices){
                data[y][x] = value
            }
        }
    }

    // applications
    fun findShortestPathBsf(
        start: Vec2I,
        end: Vec2I,
        blocker: Set<T> = setOf()
    ): List<Vec2I> {
        val visited = mutableSetOf<Vec2I>()

        val dist = mutableMapOf<Vec2I, Int>()
        val queue = mutableSetOf<Vec2I>()
        val cameFrom = mutableMapOf<Vec2I, Vec2I>()

        queue.add(start)
        dist[start] = 0

        while(queue.isNotEmpty()){
            val current = queue.minBy { dist.getOrDefault(it, Int.MAX_VALUE) }
            if(current == end){
                return reconstructPath(cameFrom, end)
            }

            queue.remove(current)
            visited.add(current)
            for (direction in Direction.entries) {
                val nextPos = current + direction
                if(nextPos in visited) continue
                if(nextPos !in this) continue
                if(this[nextPos] in blocker) continue

                dist[nextPos] = dist[current]!! + 1
                cameFrom[nextPos] = current
                queue.add(nextPos)
            }
        }

        return listOf()
    }

    private fun reconstructPath(
        cameFrom: Map<Vec2I, Vec2I>,
        end: Vec2I
    ): List<Vec2I> {
        var currentNode = end
        val path = ArrayDeque(listOf(end ))

        while (currentNode in cameFrom) {
            currentNode = cameFrom[currentNode]!!
            path.addFirst(currentNode)
        }

        return path
    }

    fun renderAsString(
        renderer: (T) -> Char,
        pathSpecs: List<Pair<List<Vec2I>, Char>> = listOf()
    ): String {
        val sb = StringBuilder()
        for (y in 0..<height) {
            for (x in 0..<width) {
                val pos = Vec2I(x, y)
                var inPaths = false
                for (pathSpec in pathSpecs) {
                    if (pos in pathSpec.first) {
                        sb.append(pathSpec.second)
                        inPaths = true
                        break
                    }
                }

                if (!inPaths) {
                    sb.append(renderer(this[pos]))
                }
            }
            sb.append("\n")
        }

        return sb.toString().trimEnd()
    }
}