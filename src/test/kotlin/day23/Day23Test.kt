package day23

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import utils.println
import utils.resourceAsLines

class Day23Test {
    @Test
    @DisplayName("Part 1 test 1")
    fun part1_test1() {
        val uut = Day23(this.resourceAsLines("test1.txt"))
        //
        val actual = uut.part1()
        //
        assertThat(actual).isEqualTo(7)
    }

    @Test
    @DisplayName("Part 1 input")
    fun part1() {
        val uut = Day23(this.resourceAsLines("input.txt"))
        //
        val actual = uut.part1()
        //
        assertThat(actual).isEqualTo(1476)
    }

    @Test
    @DisplayName("Part 2 test 1")
    fun part2_test1() {
        val uut = Day23(this.resourceAsLines("test1.txt"))
        //
        val actual = uut.part2()
        //
        assertThat(actual).isEqualTo("co,de,ka,ta")
    }

    @Test
    @DisplayName("Part 2 input")
    fun part2() {
        val uut = Day23(this.resourceAsLines("input.txt"))
        //
        val actual = uut.part2()
        //
        assertThat(actual).isEqualTo("ca,dw,fo,if,ji,kg,ks,oe,ov,sb,ud,vr,xr")
    }
}