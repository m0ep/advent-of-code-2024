package utils

import org.junit.jupiter.api.Test

class GraphsTest {
    @Test
    fun findMaxCliques() {
        val n = mapOf(
            1 to setOf(2, 5),
            2 to setOf(1, 3, 5),
            3 to setOf(2, 4),
            4 to setOf(3, 5, 6),
            5 to setOf(1, 2, 4),
            6 to setOf(4)
        )

        Graphs.findAllMaxCliques(n).forEach(::println)
    }
}