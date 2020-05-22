package aoc2019

sealed class FunkyOpCode {
    sealed class BinaryOp : FunkyOpCode() {
        object PLUS : BinaryOp()
        object MULT : BinaryOp()
        object LT : BinaryOp()
        object EQ : BinaryOp()
    }

    sealed class IoOp : FunkyOpCode() {
        object IN : IoOp()
        object OUT : IoOp()
    }

    sealed class ConditionOp : FunkyOpCode() {
        object JIT : ConditionOp()
        object JIF : ConditionOp()
    }

    object NINENINE : FunkyOpCode()

    companion object {
        fun parse(i: Int): FunkyOpCode = when (i) {
            1 -> BinaryOp.PLUS
            2 -> BinaryOp.MULT
            3 -> IoOp.IN
            4 -> IoOp.OUT
            5 -> ConditionOp.JIT
            6 -> ConditionOp.JIF
            7 -> BinaryOp.LT
            8 -> BinaryOp.EQ
            99 -> NINENINE
            else -> throw UnsupportedOperationException("Bad opcode yo")
        }
    }
}

sealed class Mode {
    object POS : Mode()
    object IMM : Mode()

    companion object {
        fun parse(i: Int): Mode = when (i) {
            0 -> POS
            1 -> IMM
            else -> throw UnsupportedOperationException("Bad mode yo")
        }
    }
}

data class Instruction(val opCode: FunkyOpCode, val mode1: Mode, val mode2: Mode, val mode3: Mode) {
    companion object {
        fun parseInstruction(i: Int): Instruction {
            val opcode = FunkyOpCode.parse(i % 100)
            val mode1 = Mode.parse((i / 100) % 10)
            val mode2 = Mode.parse((i / 1000) % 10)
            val mode3 = Mode.parse(i / 10000)
            return Instruction(opcode, mode1, mode2, mode3)
        }
    }
}

data class State(
        val machine: List<Int>,
        val pointer: Int,
        val inputCodes: List<Int>,
        val output: List<Int>,
        val ioBlocked: Boolean,
        val isTerminated: Boolean
)

class Parser {
    private fun binaryOp(instruction: Instruction, state: State): State {
        val currPointer = state.pointer
        val machine = state.machine

        val p1 = machine[currPointer + 1]
        val p2 = machine[currPointer + 2]
        val p3 = machine[currPointer + 3]

        if (instruction.mode3 is Mode.IMM)
            throw UnsupportedOperationException("Bad binary instruction yo")
        if (p3 > machine.size) throw UnsupportedOperationException("Bad place to add new yo")

        val param1 = if (instruction.mode1 is Mode.POS) machine[p1] else p1
        val param2 = if (instruction.mode2 is Mode.POS) machine[p2] else p2
        val res = when (instruction.opCode as FunkyOpCode.BinaryOp) {
            FunkyOpCode.BinaryOp.PLUS -> (param1 + param2)
            FunkyOpCode.BinaryOp.MULT -> (param1 * param2)
            FunkyOpCode.BinaryOp.LT -> if (param1 < param2) 1 else 0
            FunkyOpCode.BinaryOp.EQ -> if (param1 == param2) 1 else 0
        }

        val newMachine = machine.mapIndexed { i, code -> if (i == p3) res else code } +
                (if (p3 == machine.size) listOf(res) else listOf())
        return state.copy(machine = newMachine, pointer = currPointer + 4)
    }

    private fun ioOp(instruction: Instruction, state: State): State {
        val currPointer = state.pointer
        val machine = state.machine

        val p1 = machine[currPointer + 1]

        return if (instruction.opCode is FunkyOpCode.IoOp.IN) {
            if (p1 > machine.size) throw UnsupportedOperationException("Bad place to add new yo")

            val inputCodes = state.inputCodes
            if (inputCodes.isEmpty()) return state.copy(ioBlocked = true)

            val res = inputCodes[0]
            val newMachine = machine.mapIndexed { i, code -> if (i == p1) res else code } +
                    (if (p1 == machine.size) listOf(res) else listOf())
            state.copy(machine = newMachine, pointer = currPointer + 2,
                    inputCodes = inputCodes.drop(1))
        } else {
            val out = if (instruction.mode1 is Mode.POS) machine[p1] else p1
            val newOutput = state.output + listOf(out)
            state.copy(pointer = currPointer + 2, output = newOutput)
        }
    }

    private fun condOp(instruction: Instruction, state: State): State {
        val machine = state.machine
        val currentPointer = state.pointer

        val p1 = machine[currentPointer + 1]
        val p2 = machine[currentPointer + 2]

        val para1 = if (instruction.mode1 is Mode.POS) machine[p1] else p1
        val para2 = if (instruction.mode2 is Mode.POS) machine[p2] else p2

        val newPointer = if (para1 != 0 && instruction.opCode is FunkyOpCode.ConditionOp.JIT) para2
        else if (para1 == 0 && instruction.opCode is FunkyOpCode.ConditionOp.JIF) para2
        else currentPointer + 3

        return state.copy(pointer = newPointer)
    }

    tailrec fun parse(state: State): State {
        val machine = state.machine
        val currPointer = state.pointer

        if (state.ioBlocked) return state
        if (state.isTerminated) return state

        val instruction = Instruction.parseInstruction(machine[currPointer])

        return when (instruction.opCode) {
            is FunkyOpCode.BinaryOp -> parse(binaryOp(instruction, state))
            is FunkyOpCode.IoOp -> parse(ioOp(instruction, state))
            is FunkyOpCode.ConditionOp -> parse(condOp(instruction, state))
            is FunkyOpCode.NINENINE -> state.copy(isTerminated = true)
        }
    }

}

object FunkyPuter {
    fun runOnState(input: State): State = Parser().parse(input)

    fun runAndShowLastOutput(input: String, inputCodes: List<Int>): Int = Parser().parse(
            State(input.split(",").map { it.toInt() }, 0,
                    inputCodes, listOf(), ioBlocked = false, isTerminated = false))
            .output.last()
}

fun main() {
    val input = "3,225,1,225,6,6,1100,1,238,225,104,0,1101,34,7,225,101,17,169,224,1001,224,-92,224,4,224,1002,223,8,223,1001,224,6,224,1,224,223,223,1102,46,28,225,1102,66,83,225,2,174,143,224,1001,224,-3280,224,4,224,1002,223,8,223,1001,224,2,224,1,224,223,223,1101,19,83,224,101,-102,224,224,4,224,102,8,223,223,101,5,224,224,1,223,224,223,1001,114,17,224,1001,224,-63,224,4,224,1002,223,8,223,1001,224,3,224,1,223,224,223,1102,60,46,225,1101,7,44,225,1002,40,64,224,1001,224,-1792,224,4,224,102,8,223,223,101,4,224,224,1,223,224,223,1101,80,27,225,1,118,44,224,101,-127,224,224,4,224,102,8,223,223,101,5,224,224,1,223,224,223,1102,75,82,225,1101,40,41,225,1102,22,61,224,1001,224,-1342,224,4,224,102,8,223,223,1001,224,6,224,1,223,224,223,102,73,14,224,1001,224,-511,224,4,224,1002,223,8,223,101,5,224,224,1,224,223,223,4,223,99,0,0,0,677,0,0,0,0,0,0,0,0,0,0,0,1105,0,99999,1105,227,247,1105,1,99999,1005,227,99999,1005,0,256,1105,1,99999,1106,227,99999,1106,0,265,1105,1,99999,1006,0,99999,1006,227,274,1105,1,99999,1105,1,280,1105,1,99999,1,225,225,225,1101,294,0,0,105,1,0,1105,1,99999,1106,0,300,1105,1,99999,1,225,225,225,1101,314,0,0,106,0,0,1105,1,99999,1008,677,677,224,1002,223,2,223,1006,224,329,1001,223,1,223,1007,226,226,224,1002,223,2,223,1005,224,344,101,1,223,223,1008,226,226,224,1002,223,2,223,1006,224,359,101,1,223,223,8,226,677,224,102,2,223,223,1006,224,374,101,1,223,223,1107,677,226,224,1002,223,2,223,1005,224,389,101,1,223,223,1008,677,226,224,102,2,223,223,1006,224,404,1001,223,1,223,1108,677,677,224,102,2,223,223,1005,224,419,1001,223,1,223,1107,677,677,224,102,2,223,223,1006,224,434,1001,223,1,223,1108,226,677,224,1002,223,2,223,1006,224,449,101,1,223,223,8,677,226,224,1002,223,2,223,1005,224,464,101,1,223,223,108,226,677,224,102,2,223,223,1005,224,479,1001,223,1,223,1107,226,677,224,102,2,223,223,1005,224,494,101,1,223,223,108,677,677,224,1002,223,2,223,1005,224,509,1001,223,1,223,7,677,226,224,1002,223,2,223,1006,224,524,101,1,223,223,1007,677,677,224,1002,223,2,223,1006,224,539,1001,223,1,223,107,226,226,224,102,2,223,223,1006,224,554,101,1,223,223,107,677,677,224,102,2,223,223,1006,224,569,1001,223,1,223,1007,226,677,224,1002,223,2,223,1006,224,584,101,1,223,223,108,226,226,224,102,2,223,223,1006,224,599,1001,223,1,223,7,226,226,224,102,2,223,223,1006,224,614,1001,223,1,223,8,226,226,224,1002,223,2,223,1006,224,629,1001,223,1,223,7,226,677,224,1002,223,2,223,1005,224,644,101,1,223,223,1108,677,226,224,102,2,223,223,1006,224,659,101,1,223,223,107,226,677,224,102,2,223,223,1006,224,674,1001,223,1,223,4,223,99,226"
    println(FunkyPuter.runAndShowLastOutput(input, listOf(5)))
}
