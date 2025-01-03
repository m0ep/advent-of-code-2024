package utils.graph.clique

import utils.FindMaxCliquesAlgorithm

class FindMaxCliquesBronKerboschNoPivot<T>: FindMaxCliquesAlgorithm<T> {
    override fun run(
        neighbors: Map<T, Set<T>>,
        maxSize: Int
    ): Set<Set<T>> {
        return bronKerboschNoPivot(setOf(), neighbors.keys, setOf(), neighbors, maxSize)
    }

    private fun <T> bronKerboschNoPivot(
        r: Set<T>,
        p: Set<T>,
        x: Set<T>,
        n: Map<T, Set<T>>,
        maxSize: Int
    ): Set<Set<T>> {
        if (p.isEmpty() && x.isEmpty()) {
            return setOf(r)
        } else if(maxSize == r.size){
            return setOf(r)
        }

        var nextP = p
        var nextX = x
        val results = mutableSetOf<Set<T>>()
        for (v in nextP) {
            val vn = n[v] ?: setOf()
            bronKerboschNoPivot(r + v, nextP.intersect(vn), nextX.intersect(vn), n, maxSize)
                .let(results::addAll)
            nextP = nextP - (v)
            nextX = nextX + v
        }

        return results
    }
}