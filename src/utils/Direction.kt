package utils

enum class Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    fun toVec2I(): Vec2I {
        return when (this) {
            NORTH -> Vec2I(0, -1)
            EAST -> Vec2I(1, 0)
            SOUTH -> Vec2I(0, 1)
            WEST -> Vec2I(-1, 0)
        }
    }

    fun toVec2L(): Vec2L {
        return when (this) {
            NORTH -> Vec2L(0L, -1L)
            EAST -> Vec2L(1L, 0L)
            SOUTH -> Vec2L(0L, 1L)
            WEST -> Vec2L(-1L, 0L)
        }
    }

    fun toChar():Char{
        return when(this){
            NORTH -> '^'
            EAST -> '>'
            SOUTH -> 'v'
            WEST -> '<'
        }
    }
}