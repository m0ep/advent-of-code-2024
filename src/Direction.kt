enum class Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    fun toVec2(): Vec2 {
        return when (this) {
            NORTH -> Vec2(0, -1)
            EAST -> Vec2(1, 0)
            SOUTH -> Vec2(0, 1)
            WEST -> Vec2(-1, 0)
        }
    }
}