package day17

import utils.checkResult
import utils.pow
import utils.println
import utils.readInputLines

enum class OpMode {
    LITERAL,
    COMBO,
    IGNORE
}

enum class Instruction(
    val opCode: Int,
    val opMode: OpMode
) {
    ADV(0, OpMode.COMBO),
    BXL(1, OpMode.LITERAL),
    BST(2, OpMode.COMBO),
    JNZ(3, OpMode.LITERAL),
    BXC(4, OpMode.IGNORE),
    OUT(5, OpMode.COMBO),
    BDV(6, OpMode.COMBO),
    CDV(7, OpMode.COMBO)
}

data class Command(
    val instruction: Instruction,
    val operand: ULong
)

class Cpu(
    var a: ULong = 0UL,
    var b: ULong = 0UL,
    var c: ULong = 0UL,
    var ip: Int = 0,
    var flags: Int = 0
) {
    fun step(
        programm: List<Int>,
        output: MutableList<Int>
    ) {
        if (isHalted()) {
            return
        }

        val instruction = Instruction.entries.firstOrNull { it.opCode == programm[ip] }
        if (null == instruction) {
            flags = flags or FLAG_INVALID_OPCODE or FLAG_HALTED
            return
        }

        val operand = programm[ip + 1].toULong()
        execute(Command(instruction, operand), output)
        if (0 == flags and FLAG_BRANCH) {
            // no branching, normal progression
            ip += 2
        } else {
            // clear branch flag
            flags = flags xor FLAG_BRANCH
        }

        if (ip >= programm.size) {
            flags = flags or FLAG_HALTED
        }
    }

    private fun execute(
        command: Command,
        output: MutableList<Int>
    ) {
        val ov = operandFromCommand(command)
        when (command.instruction) {
            Instruction.ADV -> a /= ov.toInt().pow(2).toULong()
            Instruction.BXL -> b = b xor ov
            Instruction.BST -> b = ov % 8UL
            Instruction.JNZ -> {
                if (0UL != a) {
                    ip = ov.toInt()
                    flags = flags or FLAG_BRANCH
                }
            }

            Instruction.BXC -> b = b xor c
            Instruction.OUT -> output.add((ov % 8UL).toInt())
            Instruction.BDV -> b = a / ov.toInt().pow(2).toULong()
            Instruction.CDV -> c = a / ov.toInt().pow(2).toULong()
        }
    }

    fun runProgramm(
        programm: List<Int>,
        debug: Boolean = false
    ): List<Int> {
        var steps = 0
        val output = mutableListOf<Int>()

        val printState = { n: Int ->
            if (debug) {
                val stepStr = "%08d".format(n)
                "$stepStr: $this output=$output".println()
            }
        }

        printState(steps)
        while (!this.isHalted()) {
            steps++
            this.step(programm, output)
            printState(steps)
        }

        return output
    }

    private fun operandFromCommand(
        command: Command
    ): ULong {
        if (OpMode.LITERAL == command.instruction.opMode) {
            return command.operand
        }

        return when (command.operand) {
            0UL, 1UL, 2UL, 3UL -> command.operand
            4UL -> a
            5UL -> b
            6UL -> c
            else -> throw AssertionError("Invalid operand for COMBO mode '${command.operand}'")
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        return sb.append(if (isHalted()) 'H' else '_')
            .append(if (0 != flags and FLAG_INVALID_OPCODE) 'I' else '_')
            .append(if (0 != flags and FLAG_BRANCH) 'B' else '_')
            .append(" ")
            .append("IP: ").append("%08d".format(ip)).append(" ")
            .append("A: ").append("%08d".format(a)).append(" ")
            .append("B: ").append("%08d".format(b)).append(" ")
            .append("C: ").append("%08d".format(c))
            .toString()
    }

    fun isHalted() = 0 != (flags and FLAG_HALTED)

    companion object {
        const val FLAG_HALTED = 1
        const val FLAG_INVALID_OPCODE = 2
        const val FLAG_BRANCH = 3
    }
}

data class Input(
    val a: ULong = 0UL,
    val b: ULong = 0UL,
    val c: ULong = 0UL,
    val program: List<Int> = listOf()
) {

    fun toCpu(): Cpu = Cpu(a = a, b = b, c = c)

    companion object {
        fun parse(
            name: String
        ): Input {
            val lines = readInputLines(name)

            var input = Input()
            for (line in lines) {
                if (line.startsWith("Register A:")) {
                    val value = line.substring("Register A: ".length).toULong()
                    input = input.copy(a = value)
                } else if (line.startsWith("Register B:")) {
                    val value = line.substring("Register B: ".length).toULong()
                    input = input.copy(b = value)
                } else if (line.startsWith("Register C:")) {
                    val value = line.substring("Register C: ".length).toULong()
                    input = input.copy(c = value)
                } else if (line.startsWith("Program:")) {
                    val value = line.substring("Program: ".length)
                        .split(",")
                        .map(String::toInt)
                    input = input.copy(program = value)
                }
            }

            return input
        }
    }
}




fun Pair<Cpu, List<Int>>.checkRun(
    a: ULong = ULong.MIN_VALUE,
    b: ULong = ULong.MIN_VALUE,
    c: ULong = ULong.MIN_VALUE,
    ip: Int = Int.MIN_VALUE,
    flags: Int = Int.MIN_VALUE,
    output: List<Int>? = null
) {
    val (cpu, cpuOutput) = this

    if (ULong.MIN_VALUE != a) {
        if (cpu.a != a) throw AssertionError("Register a is not '$a' was ${cpu.a}")
    }

    if (ULong.MIN_VALUE != b) {
        if (cpu.b != b) throw AssertionError("Register b is not '$b' was ${cpu.b}")
    }

    if (ULong.MIN_VALUE != c) {
        if (cpu.c != c) throw AssertionError("Register c is not '$c' was ${cpu.c}")
    }

    if (Int.MIN_VALUE != ip) {
        if (cpu.ip != ip) throw AssertionError("IP is not '$ip' was ${cpu.ip}")
    }

    if (Int.MIN_VALUE != flags) {
        if (cpu.flags != flags) throw AssertionError("Flags are not '$flags' but ${cpu.flags}")
    }

    if (null != output) {
        if (cpuOutput != output) throw AssertionError("Output was not $output but was $cpuOutput")
    }
}


fun main() {
    fun part1(
        input: Input
    ): String {
        val cpu = input.toCpu()
        val program = input.program
        return cpu.runProgramm(program).joinToString(",")
    }

    fun part2(
        input: Input
    ): ULong {
        val program = input.program

        var a = 0UL
        while (true) {
            val cpu = Cpu(a = a, b = input.b, c = input.c)
            val output = cpu.runProgramm(program)

            // try to find output at the end of programm
            val lastN = program.subList(program.size - output.size, program.size)
            if (output == lastN) {
                if (output.size == program.size) {
                    break
                }

                a = (a * 8UL) // pattern repeats every 3 bit
            } else {
                a++ // try next
            }
        }

        return a
    }

    // simple test
    Cpu(c = 9UL).let { it to it.runProgramm(listOf(2, 6))}.checkRun(b = 1UL)
    Cpu(a = 10UL).let { it to it.runProgramm(listOf(5, 0, 5, 1, 5, 4)) }.checkRun(output = listOf(0, 1, 2))
    Cpu(a = 2024UL).let { it to it.runProgramm(listOf(0, 1, 5, 4, 3, 0)) }        .checkRun(a = 0UL, output = listOf(4, 2, 5, 6, 7, 7, 7, 7, 3, 1, 0))
    Cpu(b = 29UL).let { it to it.runProgramm(listOf(1, 7)) }.checkRun(b = 26UL)
    Cpu(b = 2024UL, c = 43690UL).let { it to it.runProgramm(listOf(4, 0)) }.checkRun(b = 44354UL)

    // Part 1 example
    part1(Input.parse("day17/part1_test1")).checkResult("4,6,3,5,6,3,5,2,1,0")

    // Puzzle input
    part1(Input.parse("day17/Day17")).println("Part1")
    part2(Input.parse("day17/Day17")).println("Part2")
}
