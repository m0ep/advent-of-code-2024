package day25

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import utils.println
import utils.resourceAsLines
import kotlin.time.measureTimedValue

class Day25Test {
    @Test
    @DisplayName("Part 1 test 1")
    fun part1_test1() {
        val uut = Day25(this.resourceAsLines("test1.txt"))
        //
        val actual = uut.part1()
        //
        assertThat(actual).isEqualTo(3)
    }

    @Test
    @DisplayName("Part 1 input")
    fun part1() {
        val uut = Day25(this.resourceAsLines("input.txt"))
        //
        val (actual, duration) = measureTimedValue { uut.part1()}
        "took $duration".println()
        //
        assertThat(actual).isEqualTo(2840)
    }

    @Test
    @DisplayName("Part 1 input (bruteforce)")
    fun part1Bruteforce() {
        val uut = Day25(this.resourceAsLines("input.txt"))
        //
        val (actual, duration) = measureTimedValue { uut.part1Bruteforce()}
        "took $duration".println()
        //
        assertThat(actual).isEqualTo(2840)
    }
}