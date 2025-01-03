package utils

import utils.graph.clique.FindMaxCliquesBronKerboschNoPivot

interface FindMaxCliquesAlgorithm<T>{
    fun run(
        neighbors: Map<T, Set<T>>,
        maxSize: Int = Int.MAX_VALUE
    ): Set<Set<T>>
}

class Graphs {
    companion object {
        fun <T> findAllMaxCliques(
            neighbors: Map<T, Set<T>>,
            algorithm: FindMaxCliquesAlgorithm<T> = FindMaxCliquesBronKerboschNoPivot()
        ): Set<Set<T>> = algorithm.run(neighbors)

        fun <T> findAllCliquesOfSize(
            neighbors: Map<T, Set<T>>,
            size: Int,
            algorithm: FindMaxCliquesAlgorithm<T> = FindMaxCliquesBronKerboschNoPivot()
        ): Set<Set<T>> = algorithm.run(neighbors, size).filter { size == it.size }.toSet()
    }
}
