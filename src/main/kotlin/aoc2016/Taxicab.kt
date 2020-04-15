package aoc2016

import kotlin.math.*

enum class Direction {
    N, E, S, W
}

class Taxicab(private val input: String) {
    private fun processInput(): List<String> {
        return input.split(", ")
    }

    private fun processInstruction(input: String): Pair<Int, Int> {
        val direction = when (input[0]) {
            'R' -> 1
            else -> -1
        }
        return Pair(direction, input.drop(1).toInt())
    }

    private fun ordinaryToDirection(o: Int): Direction {
        val updateO = if (o < 0) o + 4 else o % 4
        return Direction.values()[updateO]
    }

    private fun getNewPos(currentPos: Pair<Int, Int>, face: Direction, instruction: String):
            Pair<Direction, Pair<Int, Int>> {
        val (d, s) = processInstruction(instruction)
        val newFace = ordinaryToDirection(face.ordinal + d)
        val (x, y) = currentPos
        val newPos = when (newFace) {
            Direction.N -> Pair(x, y + s)
            Direction.E -> Pair(x + s, y)
            Direction.S -> Pair(x, y - s)
            else -> Pair(x - s, y)
        }
        return Pair(newFace, newPos)
    }

    private fun getSet(currentPos: Pair<Int, Int>, newPos: Pair<Int, Int>): Set<Pair<Int, Int>> {
        val (currX, currY) = currentPos
        val (newX, newY) = newPos
        val range = if (currX == newX) min(currY, newY)..max(currY, newY) else
            min(currX, newX)..max(currX, newX)
        return (if (currX == newX) range.map { Pair(currX, it) }.toSet() else
            range.map { Pair(it, currY) }.toSet()) subtract setOf(currentPos)
    }

    private tailrec fun moveHelper(currentPos: Pair<Int, Int>, face: Direction,
                                   instructions: List<String>): Pair<Int, Int> {
        if (instructions.isEmpty()) return currentPos

        val (newFace, newPos) = getNewPos(currentPos, face, instructions[0])
        return moveHelper(newPos, newFace, instructions.drop(1))
    }

    private tailrec fun checkHelper(currentPos: Pair<Int, Int>, face: Direction,
                                    instructions: List<String>, been: Set<Pair<Int, Int>>):
            Pair<Int, Int> {

        val (newFace, newPos) = getNewPos(currentPos, face, instructions[0])
        val newSet = getSet(currentPos, newPos)
        val intersection = newSet intersect been
        return if (intersection.isEmpty())
            checkHelper(newPos, newFace, instructions.drop(1), been union newSet)
        else
            intersection.first()

    }

    private fun manhattanDistance(pos: Pair<Int, Int>): Int {
        return abs(pos.first) + abs(pos.second)
    }

    fun move(): Int = manhattanDistance(moveHelper(Pair(0, 0), Direction.N, processInput()))

    fun check(): Int = manhattanDistance(checkHelper(Pair(0, 0), Direction.N, processInput(), setOf(Pair(0, 0))))

}

fun main() {
    val input = "R3, L5, R2, L1, L2, R5, L2, R2, L2, L2, L1, R2, L2, R4, R4, R1, L2, L3, R3, L1, R2, L2, L4, R4, R5, L3, R3, L3, L3, R4, R5, L3, R3, L5, L1, L2, R2, L1, R3, R1, L1, R187, L1, R2, R47, L5, L1, L2, R4, R3, L3, R3, R4, R1, R3, L1, L4, L1, R2, L1, R4, R5, L1, R77, L5, L4, R3, L2, R4, R5, R5, L2, L2, R2, R5, L2, R194, R5, L2, R4, L5, L4, L2, R5, L3, L2, L5, R5, R2, L3, R3, R1, L4, R2, L1, R5, L1, R5, L1, L1, R3, L1, R5, R2, R5, R5, L4, L5, L5, L5, R3, L2, L5, L4, R3, R1, R1, R4, L2, L4, R5, R5, R4, L2, L2, R5, R5, L5, L2, R4, R4, L4, R1, L3, R1, L1, L1, L1, L4, R5, R4, L4, L4, R5, R3, L2, L2, R3, R1, R4, L3, R1, L4, R3, L3, L2, R2, R2, R2, L1, L4, R3, R2, R2, L3, R2, L3, L2, R4, L2, R3, L4, R5, R4, R1, R5, R3"
    val cab = Taxicab(input)
    println(cab.move())
    println(cab.check())
}
