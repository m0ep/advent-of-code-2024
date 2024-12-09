#set( $Code = "bar" )
fun main() {
    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    part1(listOf("...")).checkResult(1)

    val input = readInputLines("day${Day}/Day${Day}")
    part1(input).println()
    part2(input).println()
}
