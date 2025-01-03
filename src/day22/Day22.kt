package day22

import utils.checkResult
import utils.println
import utils.readInputLines
import utils.rndToInt

data class Input(
    val seeds: List<ULong>
) {
    companion object {
        fun parse(name: String): Input {
            return readInputLines(name)
                .filter(String::isNotBlank)
                .map(String::toULong)
                .let { Input(it) }
        }
    }
}

fun nextSecret(
    seed: ULong
): ULong {
    val mix = { a: ULong, b: ULong -> a xor b }
    val prune = { a: ULong -> a and 16777215UL }

    var secret = prune(mix(seed, seed.shl(6)))
    secret = prune(mix(secret, secret.shr(5).rndToInt()))
    secret = prune(mix(secret, secret.shl(11)))
    return secret
}

fun ULong.toBananaPrice() = this % 10UL

fun main() {
    fun part1(input: Input): ULong {
        var total = 0UL
        for (seed in input.seeds) {
            val secret = (0 until 2000).fold(seed) { a, _ -> nextSecret(a) }
            total += secret
        }

        return total
    }

    fun part2(input: Input): ULong {
        val pricesOfBuyers = mutableListOf<List<Pair<ULong, Int>>>()

        for (seed in input.seeds) {
            val priceChanges = mutableListOf<Pair<ULong, Int>>()
            var currentSeed = seed
            var currentSeedPrice = seed.toBananaPrice()
            for (n in (0 until 2000)) {
                val secret = nextSecret(currentSeed)
                val secretPrice = secret.toBananaPrice()
                val diff = secretPrice.toInt() - currentSeedPrice.toInt()

                currentSeed = secret
                currentSeedPrice = secretPrice
                priceChanges.add(secretPrice to diff)
            }

            pricesOfBuyers.add(priceChanges)
        }

        val sequenceToPrice = mutableMapOf<List<Int>, ULong>()
        for (prices in pricesOfBuyers) {
            val found = mutableSetOf<List<Int>>()

            prices.windowed(4)
                .map { p -> p.last().first to p.map { it.second } }
                .forEach {
                    if (it.second !in found) {
                        found.add(it.second)
                        sequenceToPrice.merge(it.second, it.first, ULong::plus)
                    }
                }
        }

        val max = sequenceToPrice.entries.maxBy { it.value }
        return max.value
    }

    part1(Input.parse("day22/part1_test1")).checkResult(37327623UL)
    part1(Input.parse("day22/Day22")).println("Part1 input")

    part2(Input(listOf(1UL, 2UL, 3UL, 2024UL))).checkResult(23UL)
    part2(Input.parse("day22/Day22")).println("Part2 input")
}
