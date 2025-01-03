import kotlin.math.abs

data class Vec2(
    val x: Int,
    val y: Int,
) {
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun unaryMinus() = Vec2(-x, -y)
    operator fun times(scalar: Int) = Vec2(x * scalar, y * scalar)

    fun distSqrt(other: Vec2): Int {
        val dx = other.x - this.x
        val dy = other.y - this.y
        return (dx * dx) + (dy * dy)
    }


    fun isDirectNeighbour(other: Vec2): Boolean {
        val dx = abs(this.x - other.x)
        val dy = abs(this.y - other.y)
        return 1 >= dx && 1 >= dy && !(0 == dx && 0 == dy)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vec2

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun toString(): String {
        return "($x, $y)"
    }
}