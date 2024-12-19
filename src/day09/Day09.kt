package day09

import utils.checkResult
import utils.mutableCopyOf
import utils.println
import utils.readInputString
import utils.truncate
import utils.validate
import kotlin.math.min

private data class DataChunk(
    val id: Int,
    val dataSize: Int,
    val freeSpace: Int,
) {

    val chunkSize: Int
        get() = dataSize + freeSpace

    fun hasFreeSpace(): Boolean = 0 < freeSpace

    fun calcChecksum(firstIndex: Int): ULong {
        var result = 0UL
        for (i in 0 until dataSize) {
            result += (firstIndex + i).toULong() * id.toULong()
        }

        return result
    }

    fun toPrintString(): String {
        val idStr = id.toString()

        var idStrPart = ""
        while (idStrPart.length < dataSize) {
            idStrPart += idStr
        }

        return idStrPart.truncate(dataSize) + ".".repeat(freeSpace)
    }

    override fun toString(): String {
        return "($id|d$dataSize,f$freeSpace)"
    }
}

private fun parseDataMap(
    data: String
): List<DataChunk> {
    return data.toList()
        .chunked(2)
        .withIndex()
        .map {
            DataChunk(
                it.index,
                it.value[0].digitToInt(),
                it.value.getOrNull(1)?.digitToInt() ?: 0
            )
        }
}

private fun printDataMap(defragDataMap: List<DataChunk>) {
    defragDataMap.forEach(::print)
    "".println()
}

private fun calcChecksum(
    dataChunks: List<DataChunk>
): ULong {

    var index = 0
    var result = 0UL
    for (dataChunk in dataChunks) {
        result += dataChunk.calcChecksum(index)
        index += dataChunk.dataSize + dataChunk.freeSpace
    }

    return result
}

fun main() {
    fun part1(input: String): ULong {
        val dataMap = parseDataMap(input)
        if (dataMap.isEmpty()) {
            return 0UL
        }

        val defragDataMap = dataMap.mutableCopyOf()
        var lastFreeIdx = 0
        while (true) {
            var firstFreeIdx = -1
            for (idx in lastFreeIdx until defragDataMap.size) {
                if (defragDataMap[idx].hasFreeSpace()) {
                    firstFreeIdx = idx
                    break
                }
            }

            if (0 > firstFreeIdx || firstFreeIdx == defragDataMap.lastIndex) {
                break
            }

            lastFreeIdx = firstFreeIdx
            val freeChunk = defragDataMap[firstFreeIdx]
            val dataChunk = defragDataMap.removeLast()

            val defragDataSize = min(freeChunk.freeSpace, dataChunk.dataSize)

            defragDataMap[firstFreeIdx] = freeChunk.copy(freeSpace = 0)
            defragDataMap.add(
                firstFreeIdx + 1, dataChunk.copy(
                    dataSize = defragDataSize,
                    freeSpace = freeChunk.freeSpace - defragDataSize
                )
            )

            val remainingDataSize = dataChunk.dataSize - defragDataSize
            if (0 < remainingDataSize) {
                defragDataMap.add(
                    dataChunk.copy(
                        dataSize = remainingDataSize,
                        freeSpace = dataChunk.freeSpace + defragDataSize
                    )
                )
            } else {
                val lastDataChunk = defragDataMap[defragDataMap.lastIndex]
                val remainingFreeSpace = lastDataChunk.freeSpace + dataChunk.freeSpace + defragDataSize
                defragDataMap[defragDataMap.lastIndex] = lastDataChunk.copy(freeSpace = remainingFreeSpace)
            }
        }

        //printDataMap(defragDataMap)

        return calcChecksum(defragDataMap)
    }

    fun part2(input: String): ULong {
        val dataMap = parseDataMap(input)
        if (dataMap.isEmpty()) {
            return 0UL
        }

        val defragDataMap = dataMap.mutableCopyOf()
        var lastDataIdx = defragDataMap.lastIndex
        var nextDataId = defragDataMap[lastDataIdx].id
        while (true) {
            var dataIdx = -1
            for(idx in lastDataIdx downTo 0){
                val dataChunk = defragDataMap[idx]
                if(nextDataId == dataChunk.id){
                    dataIdx = idx
                    break
                }
            }

            if (0 >= dataIdx) {
                break
            }

            lastDataIdx = dataIdx
            val dataChunk = defragDataMap[dataIdx]

            var freeChuckIdx = -1
            for (idx in 0 until dataIdx) {
                val freeChunk = defragDataMap[idx]
                if (freeChunk.freeSpace >= dataChunk.dataSize) {
                    freeChuckIdx = idx
                    break
                }
            }

            if (0 > freeChuckIdx) {
                nextDataId--
                continue
            }

            // remove old chunk before moving it
            defragDataMap.removeAt(dataIdx)

            val prevDataChunk = defragDataMap[dataIdx - 1]
            defragDataMap[dataIdx-1] = prevDataChunk.copy(freeSpace = prevDataChunk.freeSpace + dataChunk.chunkSize)

            val freeChunk = defragDataMap[freeChuckIdx]
            val remainingFreeSpace = freeChunk.freeSpace - dataChunk.dataSize

            defragDataMap[freeChuckIdx] = freeChunk.copy(freeSpace = 0)
            defragDataMap.add(freeChuckIdx + 1, dataChunk.copy(freeSpace = remainingFreeSpace))

            nextDataId--
        }

        return calcChecksum(defragDataMap)
    }

    val test1Input = readInputString("day09/Day09_test")
    part1(test1Input).checkResult(1928UL)

    val input = readInputString("day09/Day09")
    part1(input).println()

    part2(test1Input).checkResult(2858UL)

    part2(input)
        .also(::println)
        .validate({it < 9809165125055UL}, "TOO HIGH 1")
        .validate({it < 6363270878542UL}, "TOO HIGH 2")
        .println()
}
