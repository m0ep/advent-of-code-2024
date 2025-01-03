package day23

import utils.Graphs

class Day23(
    private val input: List<String>
) {
    private val links = mutableMapOf<String, Set<String>>()

    init {
        val linkList = parseNetworkLinks()
        for (link in linkList) {
            links.merge(link.first, setOf(link.second)) { a, b -> a + b }
            links.merge(link.second, setOf(link.first)) { a, b -> a + b }
        }
    }

    private fun parseNetworkLinks() = input.filter(String::isNotBlank)
        .map { it.split("-") }
        .map { it[0].trim() to it[1].trim() }

    fun part1(): Int {
        val cliquesOfThree = Graphs.findAllCliquesOfSize(links, 3)
        return cliquesOfThree.filter { it.any(::startsWithT) }.size
    }

    private fun startsWithT(value: String) = value.isNotBlank() && 't' == value[0]

    fun part2() = Graphs.findAllMaxCliques(links)
        .maxBy { it.size }
        .sorted()
        .joinToString(",")
}
