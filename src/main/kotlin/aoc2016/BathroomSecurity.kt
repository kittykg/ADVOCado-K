package aoc2016

import java.lang.UnsupportedOperationException

sealed class Instruction {
    object U : Instruction()
    object D : Instruction()
    object L : Instruction()
    object R : Instruction()

    companion object {
        private fun parseChar(c: Char): Instruction = when (c) {
            'U' -> U
            'D' -> D
            'L' -> L
            'R' -> R
            else -> throw UnsupportedOperationException("Bad instruction")
        }

        fun parseLine(s: String) = s.fold(emptyList<Instruction>()) { acc, ele ->
            acc + listOf(parseChar(ele))
        }
    }
}

data class Position(val row: Int, val col: Int) {
    operator fun plus(inc: Position) = Position(row + inc.row, col + inc.col)

    fun cap(): Position {
        val cappedRow = when {
            row < 0 -> 0
            row > 2 -> 2
            else -> row
        }
        val cappedCol = when {
            col < 0 -> 0
            col > 2 -> 2
            else -> col
        }
        return Position(cappedRow, cappedCol)
    }
}

data class State(val currPosition: Position, val currentCode: String)


class BathroomSecurity(input: String) {
    private val instructions = input.split("\n").map { Instruction.parseLine(it) }

    private val positionToNumberMap = mapOf(
            Position(0, 0) to '1',
            Position(0, 1) to '2',
            Position(0, 2) to '3',
            Position(1, 0) to '4',
            Position(1, 1) to '5',
            Position(1, 2) to '6',
            Position(2, 0) to '7',
            Position(2, 1) to '8',
            Position(2, 2) to '9'
    )

    private val positionToFunkyPadMap = mapOf(
            Position(0, 2) to '1',
            Position(1, 1) to '2',
            Position(1, 2) to '3',
            Position(1, 3) to '4',
            Position(2, 0) to '5',
            Position(2, 1) to '6',
            Position(2, 2) to '7',
            Position(2, 3) to '8',
            Position(2, 4) to '9',
            Position(3, 1) to 'A',
            Position(3, 2) to 'B',
            Position(3, 3) to 'C',
            Position(4, 2) to 'D'
    )

    private fun calcChange(instruction: Instruction) = when (instruction) {
        Instruction.U -> Position(-1, 0)
        Instruction.D -> Position(1, 0)
        Instruction.L -> Position(0, -1)
        Instruction.R -> Position(0, 1)
    }

    private fun move(instruction: Instruction, currPosition: Position): Position {
        val change = calcChange(instruction)
        return (currPosition + change).cap()
    }

    private fun funkyMove(instruction: Instruction, currPosition: Position): Position {
        val funkyPadCorner = mapOf(
                Position(0, 2) to Instruction.D,
                Position(2, 0) to Instruction.R,
                Position(2, 4) to Instruction.L,
                Position(4, 2) to Instruction.U
        )

        val funkyPadEdge = mapOf(
                Position(1, 1) to listOf(Instruction.D, Instruction.R),
                Position(1, 3) to listOf(Instruction.D, Instruction.L),
                Position(3, 1) to listOf(Instruction.U, Instruction.R),
                Position(3, 3) to listOf(Instruction.U, Instruction.L)
        )

        val movable = when (currPosition) {
            in funkyPadCorner -> funkyPadCorner[currPosition] == instruction
            in funkyPadEdge -> funkyPadEdge[currPosition]!!.contains(instruction)
            else -> true
        }
        return if (movable) calcChange(instruction) + currPosition else currPosition
    }

    private fun processInstructions(instructions: List<Instruction>, currState: State,
                                    keypadMap: Map<Position, Char>,
                                    moveFun: (Instruction, Position) -> Position): State {
        val newPos = instructions.fold(currState.currPosition) { pos, ins -> moveFun(ins, pos) }
        val newString = currState.currentCode + keypadMap[newPos]
        return State(newPos, newString)
    }

    fun getCode(): String {
        val initState = State(Position(1, 1), "")
        val finalState = instructions.fold(initState) { acc, list ->
            processInstructions(list, acc, positionToNumberMap, ::move)
        }
        return finalState.currentCode
    }

    fun getFunkyCode(): String {
        val initState = State(Position(2, 0), "")
        val finalState = instructions.fold(initState) { acc, list ->
            processInstructions(list, acc, positionToFunkyPadMap, ::funkyMove)
        }
        return finalState.currentCode
    }
}
