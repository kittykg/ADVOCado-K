package aoc2019

import java.math.BigInteger

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

    sealed class UnaryOp : FunkyOpCode() {
        object ADJ : UnaryOp()
    }

    object HALT : FunkyOpCode()

    companion object {
        fun parse(i: BigInteger): FunkyOpCode = when (i) {
            BigInteger.ONE -> BinaryOp.PLUS
            BigInteger.TWO -> BinaryOp.MULT
            3.toBigInteger() -> IoOp.IN
            4.toBigInteger() -> IoOp.OUT
            5.toBigInteger() -> ConditionOp.JIT
            6.toBigInteger() -> ConditionOp.JIF
            7.toBigInteger() -> BinaryOp.LT
            8.toBigInteger() -> BinaryOp.EQ
            9.toBigInteger() -> UnaryOp.ADJ
            99.toBigInteger() -> HALT
            else -> throw UnsupportedOperationException("Bad opcode yo")
        }
    }
}

sealed class Mode {
    object POS : Mode()
    object IMM : Mode()
    object REL : Mode()

    companion object {
        fun parse(i: BigInteger): Mode = when (i) {
            BigInteger.ZERO -> POS
            BigInteger.ONE -> IMM
            BigInteger.TWO -> REL
            else -> throw UnsupportedOperationException("Bad mode yo")
        }
    }
}

data class Instruction(val opCode: FunkyOpCode, val mode1: Mode, val mode2: Mode, val mode3: Mode) {
    companion object {
        fun parseInstruction(i: BigInteger): Instruction {
            val opcode = FunkyOpCode.parse(i % 100.toBigInteger())
            val mode1 = Mode.parse((i / 100.toBigInteger()) % 10.toBigInteger())
            val mode2 = Mode.parse((i / 1000.toBigInteger()) % 10.toBigInteger())
            val mode3 = Mode.parse(i / 10000.toBigInteger())
            return Instruction(opcode, mode1, mode2, mode3)
        }
    }
}

data class State(
        val machine: List<BigInteger>,
        val pointer: Int,
        val relativeBase: Int,
        val inputCodes: List<BigInteger>,
        val output: List<BigInteger>,
        val ioBlocked: Boolean,
        val isTerminated: Boolean
)

class Parser {
    private fun updateMachine(machine: List<BigInteger>, position: Int, newVal: BigInteger)
            : List<BigInteger> {
        val positionDiff = position - machine.size
        val extra = if (positionDiff >= 0)
            List(positionDiff) { BigInteger.ZERO } + listOf(newVal) else listOf()
        return machine.mapIndexed { i, code -> if (i == position) newVal else code } + extra
    }

    private fun getFromPos(machine: List<BigInteger>, position: BigInteger): BigInteger {
        if (position < BigInteger.ZERO) throw UnsupportedOperationException("Bad place to access yo")
        return if (position >= machine.size.toBigInteger()) BigInteger.ZERO else
            machine[position.toInt()]
    }

    private fun getParam(state: State, mode: Mode, offset: Int): BigInteger {
        val currPointer = state.pointer
        val machine = state.machine

        val p = machine[currPointer + offset]

        if (mode is Mode.POS && p < BigInteger.ZERO)
            throw throw UnsupportedOperationException("Bad place to access yo")

        if (mode is Mode.REL && (state.relativeBase + p.toInt()) < 0)
            throw throw UnsupportedOperationException("Bad place to access yo")

        return when (mode) {
            Mode.POS -> getFromPos(machine, p)
            Mode.IMM -> p
            Mode.REL -> getFromPos(machine, state.relativeBase.toBigInteger() + p)
        }
    }

    private fun getWrittenToPos(state: State, mode: Mode, offset: Int): Int {
        val currPointer = state.pointer
        val machine = state.machine

        val p = machine[currPointer + offset]

        return when (mode) {
            Mode.IMM -> throw UnsupportedOperationException("Bad mode yo")
            Mode.POS -> p.toInt()
            Mode.REL -> state.relativeBase + p.toInt()
        }
    }

    private fun binaryOp(instruction: Instruction, state: State): State {
        val currPointer = state.pointer
        val machine = state.machine


        val param1 = getParam(state, instruction.mode1, 1)
        val param2 = getParam(state, instruction.mode2, 2)
        val param3 = getWrittenToPos(state, instruction.mode3, 3)

        if (param3 < 0)
            throw throw UnsupportedOperationException("Bad place to add new yo")

        val res = when (instruction.opCode as FunkyOpCode.BinaryOp) {
            FunkyOpCode.BinaryOp.PLUS -> (param1 + param2)
            FunkyOpCode.BinaryOp.MULT -> (param1 * param2)
            FunkyOpCode.BinaryOp.LT -> if (param1 < param2) BigInteger.ONE else BigInteger.ZERO
            FunkyOpCode.BinaryOp.EQ -> if (param1 == param2) BigInteger.ONE else BigInteger.ZERO
        }

        return state.copy(machine = updateMachine(machine, param3, res), pointer = currPointer + 4)
    }

    private fun ioOp(instruction: Instruction, state: State): State {
        val currPointer = state.pointer
        val machine = state.machine

        return if (instruction.opCode is FunkyOpCode.IoOp.IN) {
            val p1 = getWrittenToPos(state, instruction.mode1, 1)

            val inputCodes = state.inputCodes
            if (inputCodes.isEmpty()) return state.copy(ioBlocked = true)
            val res = inputCodes[0]

            val newMachine = updateMachine(machine, p1, res)
            state.copy(machine = newMachine, pointer = currPointer + 2,
                    inputCodes = inputCodes.drop(1))
        } else {
            val out = getParam(state, instruction.mode1, 1)
            val newOutput = state.output + listOf(out)
            state.copy(pointer = currPointer + 2, output = newOutput)
        }
    }

    private fun condOp(instruction: Instruction, state: State): State {
        val currentPointer = state.pointer

        val para1 = getParam(state, instruction.mode1, 1)
        val para2 = getParam(state, instruction.mode2, 2).toInt()

        val newPointer = if (para1 != BigInteger.ZERO && instruction.opCode is FunkyOpCode.ConditionOp.JIT) para2
        else if (para1 == BigInteger.ZERO && instruction.opCode is FunkyOpCode.ConditionOp.JIF) para2
        else currentPointer + 3

        return state.copy(pointer = newPointer)
    }

    private fun unaryOp(instruction: Instruction, state: State): State {
        val currPointer = state.pointer
        val relativeBase = state.relativeBase

        val param1 = getParam(state, instruction.mode1, 1).toInt()

        return state.copy(pointer = currPointer + 2, relativeBase = relativeBase + param1)
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
            is FunkyOpCode.UnaryOp.ADJ -> parse(unaryOp(instruction, state))
            is FunkyOpCode.HALT -> state.copy(isTerminated = true)
        }
    }

}

object FunkyPuter {
    fun runOnInput(input: String, inputCodes: List<BigInteger> = listOf()) = Parser().parse(
            State(input.split(",").map { it.toBigInteger() }, 0, 0,
                    inputCodes, listOf(), ioBlocked = false, isTerminated = false))

    fun runOnState(input: State): State = Parser().parse(input)

    fun runAndShowLastOutput(input: String, inputCodes: List<BigInteger> = listOf()): BigInteger =
            runOnInput(input, inputCodes).output.last()
}
