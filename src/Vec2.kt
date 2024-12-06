data class Vec2(
    val x: Int,
    val y: Int,
) {
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun unaryMinus() = Vec2(-x, -y)
}