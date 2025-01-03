package day25

import kotlin.math.max

typealias Lock = List<Int>
typealias Key = List<Int>

data class LockTreeNode(
    val children: MutableMap<Int, LockTreeNode> = mutableMapOf(),
    val lock: Lock? = null
)

class Day25(
    input: List<String>
) {
    private val keys: List<Key>
    private val locks: List<Lock>
    private val lockTreeRoot = LockTreeNode()

    init {
        val keysInit = mutableListOf<Key>()
        val locksInit = mutableListOf<Lock>()

        val iterator = input.iterator()
        while (iterator.hasNext()) {
            var line = iterator.next()
            if(line.isBlank()) {
                continue
            }

            if (line == "#####") {
                var lock = List(5) { 0 }
                repeat(6) {
                    line = iterator.next()
                    lock = line.map { if ('#' == it) 1 else 0 }
                        .zip(lock)
                        .map { it.first + it.second }
                }

                locksInit += lock
            } else if (line == ".....") {
                var key = List(5) { 0 }
                repeat(6) {
                    line = iterator.next()
                    key = line.map { if ('#' == it) 1 else 0 }
                        .zip(key)
                        .map { it.first + it.second }
                }

                keysInit += key.map { max(0, it - 1) }
            }
        }

        keys = keysInit
        locks = locksInit
        for (lock in locks) {
            buildLockTree(lockTreeRoot, lock, 0)
        }
    }

    private fun buildLockTree(
        current: LockTreeNode,
        lock: Lock,
        level: Int
    ) {
        val n = lock[level]
        if (level == lock.size - 1) {
            current.children[n] = LockTreeNode(lock = lock)
        } else {
            buildLockTree(current.children.getOrPut(n) { LockTreeNode() }, lock, level + 1)
        }
    }

    private fun findNonOverlappingLock(
        current: LockTreeNode,
        key: Key,
        level: Int
    ): Set<Lock> {
        if (level == key.size) {
            return current.lock?.let { setOf(it) } ?: setOf()
        }

        val result = mutableSetOf<Lock>()
        val n = key[level]
        for ((lockPin, child) in current.children.entries) {
            if (5 >= lockPin + n) {
                findNonOverlappingLock(child, key, level + 1).let(result::addAll)
            }
        }

        return result
    }

    fun part1(): Int {
        var result = 0
        for (key in keys) {
            findNonOverlappingLock(lockTreeRoot, key, 0).let {
                result += it.size
            }
        }

        return result
    }

    fun part1Bruteforce(): Int {
        val tmp = locks.flatMap { lock ->
            keys.map { key ->
                lock.zip(key).maxOf { p -> p.first + p.second }
            }
        }

        return tmp.count { 5 >= it }
    }
}
