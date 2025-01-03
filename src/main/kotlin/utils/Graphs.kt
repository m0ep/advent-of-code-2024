package utils


class Graphs {
    companion object {
        fun <T> findMaxClique(
            neighbors: Map<T, Set<T>>
        ): Set<T> {
            val queue = ArrayDeque(neighbors.keys.map { setOf(it) })
            val found = queue.toMutableSet()

            while (queue.isNotEmpty()) {
                val clique = queue.removeFirst()
                for (node in neighbors.keys) {
                    if (true == neighbors[node]?.containsAll(clique)) {
                        val next = clique + node
                        if (next !in found) {
                            queue.addLast(next)
                            found.add(next)
                        }
                    }
                }

                if (queue.isEmpty()) {
                    return clique
                }
            }

            return emptySet()
        }

        fun <T> findCliquesOfSizeN(
            neighbors: Map<T, Set<T>>,
            n: Int
        ): Set<Set<T>> {
            val queue = ArrayDeque(neighbors.keys.map { setOf(it) })
            val found = queue.toMutableSet()
            val result = mutableSetOf<Set<T>>()

            while (queue.isNotEmpty()) {
                val clique = queue.removeFirst()
                for (node in neighbors.keys) {
                    if (true == neighbors[node]?.containsAll(clique)) {
                        val next = clique + node
                        if (next !in found) {
                            if (n == next.size) {
                                result.add(next)
                            } else {
                                found.add(next)
                                queue.addLast(next)
                            }
                        }
                    }
                }
            }

            return result
        }
    }
}
