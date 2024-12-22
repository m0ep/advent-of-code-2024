package utils

import kotlin.math.abs

data class Vec2I(
    val x: Int,
    val y: Int,
) {
    operator fun plus(other: Vec2I) = Vec2I(x + other.x, y + other.y)
    operator fun minus(other: Vec2I) = Vec2I(x - other.x, y - other.y)
    operator fun unaryMinus() = Vec2I(-x, -y)
    operator fun times(scalar: Int) = Vec2I(x * scalar, y * scalar)
    operator fun plus(direction: Direction) = this + direction.toVec2I()

    fun distSqrt(
        other: Vec2I
    ): Int {
        val dx = other.x - this.x
        val dy = other.y - this.y
        return (dx * dx) + (dy * dy)
    }

    fun isNeighbor(
        other: Vec2I
    ): Boolean {
        val dx = abs(this.x - other.x)
        val dy = abs(this.y - other.y)
        return 1 >= dx && 1 >= dy && !(0 == dx && 0 == dy)
    }

    fun neighbors(): Set<Vec2I> = Direction.entries.map { this + it }.toSet()

    override fun equals(
        other: Any?
    ): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vec2I

        if (x != other.x) return false
        if (y != other.y) return false
        return true
    }

    override fun toString(): String {
        return "($x, $y)"
    }
}