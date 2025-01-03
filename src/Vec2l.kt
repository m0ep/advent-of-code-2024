data class Vec2l(
    val x: Long,
    val y: Long,
) {
    operator fun plus(other: Vec2l) = Vec2l(x + other.x, y + other.y)
    operator fun minus(other: Vec2l) = Vec2l(x - other.x, y - other.y)
    operator fun unaryMinus() = Vec2l(-x, -y)
    operator fun times(scalar: Long) = Vec2l(x * scalar, y * scalar)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vec2l

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

}