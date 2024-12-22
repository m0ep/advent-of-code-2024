package day22

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import utils.resourceAsListOfULong

class Day22Test {
    @Test
    @DisplayName("Part 1 test 1")
    fun part1_test1() {
        val uut = Day22(listOf(1UL, 10UL, 100UL, 2024UL))
        //
        val actual = uut.part1()
        //
        assertThat(actual).isEqualTo(37_327_623UL)

    }

    @Test
    @DisplayName("Part 1 input")
    fun part1() {
        val uut = Day22(this.resourceAsListOfULong("input.txt"))
        //
        val actual = uut.part1()
        //
        assertThat(actual).isEqualTo(16_953_639_210UL)
    }

    @Test
    @DisplayName("Part 2 test 1")
    fun part2_test1() {
        val uut = Day22(listOf(1UL, 2UL, 3UL, 2024UL))
        //
        val actual = uut.part2()
        //
        assertThat(actual).isEqualTo(23UL)
    }

    @Test
    @DisplayName("Part 2 input")
    fun part2() {
        val uut = Day22(this.resourceAsListOfULong("input.txt"))
        //
        val actual = uut.part2()
        //
        assertThat(actual).isEqualTo(1_863UL)
    }
}