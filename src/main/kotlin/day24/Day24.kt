package day24

import utils.println

enum class Operation(
    val char: Char
) {
    OR('|'),
    AND('&'),
    XOR('^');

    companion object {
        fun find(value: String?, defValue: Operation): Operation {
            if (true == value?.isNotBlank()) {
                val char = value.trim().first()
                for (entry in entries) {
                    if (entry.char == char) {
                        return entry
                    }
                }
            }

            return defValue
        }
    }
}

abstract class Gate(
    val operation: Operation,
    val a: String,
    val b: String,
    val output: String
) {
    abstract fun update(
        mem: MutableMap<String, Int>
    ): Boolean

    override fun toString(): String {
        return "$a $operation $b -> $output"
    }
}

class OrGate(
    a: String,
    b: String,
    output: String
) : Gate(Operation.OR, a, b, output) {
    override fun update(
        mem: MutableMap<String, Int>
    ): Boolean {
        if (a !in mem || b !in mem) {
            return false
        }

        val a = mem[a]!!
        val b = mem[b]!!

        val oldValue = mem[output] ?: -1
        mem[output] = a or b

        return if (0 > oldValue) {
            true
        } else {
            mem[output] != oldValue
        }
    }
}

class AndGate(
    a: String,
    b: String,
    output: String
) : Gate(Operation.AND, a, b, output) {
    override fun update(
        mem: MutableMap<String, Int>
    ): Boolean {
        if (a !in mem || b !in mem) {
            return false
        }

        val a = mem[a]!!
        val b = mem[b]!!

        val oldValue = mem[output] ?: -1
        mem[output] = a and b

        return if (0 > oldValue) {
            true
        } else {
            mem[output] != oldValue
        }
    }
}

class XorGate(
    a: String,
    b: String,
    output: String
) : Gate(Operation.XOR, a, b, output) {
    override fun update(
        mem: MutableMap<String, Int>
    ): Boolean {
        if (a !in mem || b !in mem) {
            return false
        }

        val a = mem[a]!!
        val b = mem[b]!!

        val oldValue = mem[output] ?: -1
        mem[output] = a xor b

        return if (0 > oldValue) {
            true
        } else {
            mem[output] != oldValue
        }
    }
}

class Day24(
    input: List<String>
) {
    private val mem: Map<String, Int>
    private val gates: List<Gate>

    init {
        val wireRegex = "^(\\w+):\\s+([01])$".toRegex()
        val gateRegex = "^(\\w+)\\s+(XOR|OR|AND)\\s+(\\w+)\\s+->\\s+(\\w+)$".toRegex()

        val memInit = mutableMapOf<String, Int>()
        val gatesInit = mutableListOf<Gate>()
        for (line in input) {
            if (line.isBlank()) {
                continue
            }

            val wireMatch = wireRegex.find(line)
            if (null != wireMatch) {
                val (name, value) = wireMatch.destructured
                memInit[name] = value.toInt()
                continue
            }

            val gateMatch = gateRegex.find(line)
            if (null != gateMatch) {
                val (a, op, b, o) = gateMatch.destructured
                val gate = when (op) {
                    "OR" -> OrGate(a, b, o)
                    "AND" -> AndGate(a, b, o)
                    "XOR" -> XorGate(a, b, o)
                    else -> continue
                }

                gatesInit.add(gate)
            }
        }

        mem = memInit
        gates = gatesInit
    }

    fun part1(): ULong {
        val simMem = mem.toMutableMap()
        simulate(gates, simMem)
        val output = simMem.entries
            .filter { isOutputWire(it.key) }
            .sortedBy { it.key }
            .map { it.value }
        return calculateNumber(output)
    }

    private fun isOutputWire(value: String) = 'z' == value.first()

    private fun simulate(
        gates: List<Gate>,
        mem: MutableMap<String, Int>
    ) {
        val queue = ArrayDeque(gates)
        while (queue.isNotEmpty()) {
            val gate = queue.removeFirst()
            if (gate.update(mem)) {
                val inputs = gates.filter { it.a == gate.output || it.b == gate.output }
                queue.addAll(inputs)
            }
        }
    }

    private fun calculateNumber(
        bits: List<Int>
    ): ULong {
        var result = 0UL
        for (i in bits.indices) {
            result += bits[i].toULong().shl(i)
        }

        return result
    }

    fun part2(): String {
        val wires = gates.flatMap {
            listOf(it.a, it.b, it.output)
        }.distinct()

        val highestZ = wires.filter { 'z' == it.first() }.maxOf { it }
        val wrong = mutableSetOf<String>()
        for (gate in gates) {
            if (
                isOutputWire(gate.output)
                && Operation.XOR != gate.operation
                && gate.output != highestZ
            ) { // A z-wire is always the output of a XOR gate.
                // Exception is the highest bit, that comes from the highest carry
                wrong += gate.output
                continue
            }

            if (
                Operation.XOR == gate.operation
                && gate.a.first() !in setOf('x', 'y')
                && gate.b.first() !in setOf('x', 'y')
                && !isOutputWire(gate.output)
            ) { // Inputs of a XOR gate are x-wires or y-wires or the XOR gates outputs to a z.wire
                wrong += gate.output
                continue
            }

            if (
                Operation.AND == gate.operation // AND gates must be followed by a OR gate
                && "x00" !in setOf(gate.a, gate.b) // Special handling if an input is x00
            ) {
                for (subGate in gates) {
                    if (gate.output in setOf(subGate.a, subGate.b) && Operation.OR != subGate.operation) {
                        "OR must follow AND: $gate -> $subGate".println()
                        wrong += gate.output
                    }
                }
            }

            if (Operation.XOR == gate.operation) {
                // A OR gate can't follow XOR gates
                for (subGate in gates) {
                    if (gate.output in setOf(subGate.a, subGate.b) && Operation.OR == subGate.operation) {
                        wrong += gate.output
                    }
                }
            }
        }

        return wrong.sorted().joinToString(",")
    }
}
