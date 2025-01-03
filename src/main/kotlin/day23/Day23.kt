package day23

class Day23(
    private val input: List<String>
) {
    private val linkList = parseNetworkLinks()
    private val links = mutableMapOf<String, Set<String>>()

    init {
        for (link in linkList) {
            links.merge(link.first, setOf(link.second)) { a, b -> a + b }
            links.merge(link.second, setOf(link.first)) { a, b -> a + b }
        }
    }

    fun part1(): Int {
        val maxLevels = 3
        val networkLevelPaths = mutableMapOf<Int, List<List<String>>>()
        networkLevelPaths[0] = links.keys.map { listOf(it) }
        for (level in 1..maxLevels) {
            val prevNetworkPath = networkLevelPaths[level - 1] ?: continue
            val currentNetwork = mutableListOf<List<String>>()
            for (path in prevNetworkPath) {
                val lastNode = path.last()
                links[lastNode]?.let { neighbors ->
                    neighbors.forEach { currentNetwork.add(path + it) }
                }
            }

            networkLevelPaths[level] = currentNetwork
        }

        val nodesStartsWithT = { n: String -> 't' == n[0] }
        val resultList = networkLevelPaths[3]?.filter { it.first() == it.last() }
            ?.map { it.dropLast(1).sorted() }
            ?.distinct()
            ?.filter { it.any(nodesStartsWithT) }

        return resultList?.size ?: 0
    }

    private fun parseNetworkLinks() = input.filter(String::isNotBlank)
        .map { it.split("-") }
        .map { it[0].trim() to it[1].trim() }

    fun part2(): String {
        val cliques = ArrayDeque(links.keys.map { setOf(it) })
        val foundCliques = cliques.toMutableSet()

        while (cliques.isNotEmpty()){
            val clique = cliques.removeFirst()
            for(node in links.keys){
                if(true == links[node]?.containsAll(clique)){
                    val next = clique + node
                    if(next !in foundCliques) {
                        cliques.addLast(next)
                        foundCliques.add(next)
                    }
                }
            }

            if(cliques.isEmpty()){
                return clique.sorted().joinToString(",")
            }
        }

        return ""
    }
}
