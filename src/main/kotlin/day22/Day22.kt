package day22

import utils.rndToInt

class Day22(
    private val input: List<ULong>
) {
    fun part1(): ULong {
        var total = 0UL
        for (seed in input) {
            val secret = (0 until 2000).fold(seed) { a, _ -> nextSecret(a) }
            total += secret
        }

        return total
    }

    fun part2(): ULong {
        val pricesOfBuyers = mutableListOf<List<Pair<ULong, Int>>>()

        for (seed in input) {
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

    private fun nextSecret(
        seed: ULong
    ): ULong {
        val mix = { a: ULong, b: ULong -> a xor b }
        val prune = { a: ULong -> a and 16777215UL }

        var secret = prune(mix(seed, seed.shl(6)))
        secret = prune(mix(secret, secret.shr(5).rndToInt()))
        secret = prune(mix(secret, secret.shl(11)))
        return secret
    }

    private fun ULong.toBananaPrice() = this % 10UL
}
