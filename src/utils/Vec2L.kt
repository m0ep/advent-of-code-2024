package utils

import kotlin.math.abs

data class Vec2L(
    val x: Long,
    val y: Long,
) {
    operator fun plus(other: Vec2L) = Vec2L(x + other.x, y + other.y)
    operator fun minus(other: Vec2L) = Vec2L(x - other.x, y - other.y)
    operator fun unaryMinus() = Vec2L(-x, -y)
    operator fun times(scalar: Long) = Vec2L(x * scalar, y * scalar)
    operator fun plus(direction: Direction) = this + direction.toVec2L()

    fun distSqrt(
        other: Vec2L
    ): Long {
        val dx = other.x - this.x
        val dy = other.y - this.y
        return (dx * dx) + (dy * dy)
    }

    fun isNeighbor(
        other: Vec2L
    ): Boolean {
        val dx = abs(this.x - other.x)
        val dy = abs(this.y - other.y)
        return 1 >= dx && 1 >= dy && !(0L == dx && 0L == dy)
    }

    fun neighbors(): Set<Vec2L> = Direction.entries.map { this + it }.toSet()

    override fun equals(
        other: Any?
    ): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vec2L

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun toString(): String {
        return "($x, $y)"
    }
}