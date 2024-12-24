package day24

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import utils.println
import utils.resourceAsLines

class Day24Test {
    @Test
    @DisplayName("Part 1 test 1")
    fun part1_test1() {
        val uut = Day24(this.resourceAsLines("test1.txt"))
        //
        val actual = uut.part1()
        //
        assertThat(actual).isEqualTo(4UL)
    }

    @Test
    @DisplayName("Part 1 test 2")
    fun part1_test2() {
        val uut = Day24(this.resourceAsLines("test2.txt"))
        //
        val actual = uut.part1()
        //
        assertThat(actual).isEqualTo(2024UL)
    }

    @Test
    @DisplayName("Part 1 input")
    fun part1() {
        val uut = Day24(this.resourceAsLines("input.txt"))
        //
        val actual = uut.part1()
        //
        assertThat(actual).isEqualTo(55920211035878UL)
    }

    @Test
    @DisplayName("Part 2 input")
    fun part2() {
        val uut = Day24(this.resourceAsLines("input.txt"))
        //
        val actual = uut.part2()
        //
        assertThat(actual).isEqualTo("btb,cmv,mwp,rdg,rmj,z17,z23,z30")
    }
}