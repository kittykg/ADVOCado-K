package aoc2019

sealed class Colour {
    object BLACK : Colour()
    object WHITE : Colour()

    fun getCode(): Int = when (this) {
        BLACK -> 0
        WHITE -> 1
    }
}

sealed class Direction {
    object N : Direction()
    object E : Direction()
    object S : Direction()
    object W : Direction()

    fun turnLeft(): Direction = when (this) {
        N -> W
        W -> S
        S -> E
        E -> N
    }

    fun turnRight(): Direction = when (this) {
        N -> E
        E -> S
        S -> W
        W -> N
    }
}

data class Position(val x: Int, val y: Int) {
    fun move(direction: Direction): Position = when (direction) {
        Direction.N -> Position(this.x, this.y + 1)
        Direction.E -> Position(this.x + 1, this.y)
        Direction.S -> Position(this.x, this.y - 1)
        Direction.W -> Position(this.x - 1, this.y)
    }
}

data class Panel(val position: Position, val colour: Colour, val direction: Direction)

data class CanvasState(val paintedPositions: Set<Position>, val whitePositions: Set<Position>)

class PaintingRobot(private val input: String) {
    private tailrec fun draw(currPanel: Panel, currState: State, currCanvasState: CanvasState)
            : CanvasState {
        if (currState.isTerminated) return currCanvasState

        val (currPosition, currColour, currDirection) = currPanel
        val (paintedPos, whitePos) = currCanvasState
        val input = currColour.getCode().toBigInteger()

        val returnState = FunkyPuter.runOnState(
                currState.copy(inputCodes = listOf(input), output = listOf(), ioBlocked = false))
        val (paint, turn) = returnState.output.map { it.toInt() }

        // Paint at current position
        val newPosSet = setOf(currPosition)
        val newWhitePos = when (paint) {
            0 -> whitePos subtract newPosSet
            1 -> whitePos union newPosSet
            else -> throw UnsupportedOperationException("What are thoooose")
        }
        val newPaintedPos = paintedPos union newPosSet

        // Turn and move
        val newDirection = when (turn) {
            0 -> currDirection.turnLeft()
            1 -> currDirection.turnRight()
            else -> throw UnsupportedOperationException("What are thoooose")
        }
        val newPosition = currPosition.move(newDirection)
        val newPanelColour = if (newPosition in whitePos) Colour.WHITE else Colour.BLACK

        val newPanel = Panel(newPosition, newPanelColour, newDirection)
        val newCanvasState = CanvasState(newPaintedPos, newWhitePos)

        return draw(newPanel, returnState, newCanvasState)
    }

    fun startDrawing(startColour: Colour): CanvasState {
        val initPanel = Panel(Position(0, 0), startColour, Direction.N)
        val initState = FunkyPuter.initialise(input, listOf())
        val initWhitePos = if (startColour is Colour.WHITE) setOf(Position(0, 0)) else setOf()
        val initCanvasState = CanvasState(setOf(), initWhitePos)

        return draw(initPanel, initState, initCanvasState)
    }
}

fun printCanvas(whitePositions: Set<Position>) {
    val maxX = whitePositions.maxBy { it.x }!!.x
    val maxY = whitePositions.maxBy { it.y }!!.y
    val minX = whitePositions.minBy { it.x }!!.x
    val minY = whitePositions.minBy { it.y }!!.y

    (maxY downTo minY).map { y ->
        (minX..maxX).map { x ->
            if (Position(x, y) in whitePositions) print("#") else print(".")
        }
        print("\n")
    }
}

fun main() {
    val input = "3,8,1005,8,314,1106,0,11,0,0,0,104,1,104,0,3,8,1002,8,-1,10,1001,10,1,10,4,10,108,1,8,10,4,10,1002,8,1,28,2,2,16,10,1,1108,7,10,1006,0,10,1,5,14,10,3,8,102,-1,8,10,101,1,10,10,4,10,108,1,8,10,4,10,102,1,8,65,1006,0,59,2,109,1,10,1006,0,51,2,1003,12,10,3,8,102,-1,8,10,1001,10,1,10,4,10,108,1,8,10,4,10,1001,8,0,101,1006,0,34,1,1106,0,10,1,1101,17,10,3,8,102,-1,8,10,101,1,10,10,4,10,1008,8,0,10,4,10,1001,8,0,135,3,8,1002,8,-1,10,101,1,10,10,4,10,108,0,8,10,4,10,1001,8,0,156,3,8,1002,8,-1,10,101,1,10,10,4,10,108,0,8,10,4,10,1001,8,0,178,1,108,19,10,3,8,102,-1,8,10,101,1,10,10,4,10,108,0,8,10,4,10,1002,8,1,204,1,1006,17,10,3,8,102,-1,8,10,101,1,10,10,4,10,108,1,8,10,4,10,102,1,8,230,1006,0,67,1,103,11,10,1,1009,19,10,1,109,10,10,3,8,102,-1,8,10,101,1,10,10,4,10,1008,8,0,10,4,10,101,0,8,268,3,8,102,-1,8,10,101,1,10,10,4,10,1008,8,1,10,4,10,1002,8,1,290,2,108,13,10,101,1,9,9,1007,9,989,10,1005,10,15,99,109,636,104,0,104,1,21101,48210224024,0,1,21101,0,331,0,1105,1,435,21101,0,937264165644,1,21101,0,342,0,1105,1,435,3,10,104,0,104,1,3,10,104,0,104,0,3,10,104,0,104,1,3,10,104,0,104,1,3,10,104,0,104,0,3,10,104,0,104,1,21101,235354025051,0,1,21101,389,0,0,1105,1,435,21102,29166169280,1,1,21102,400,1,0,1105,1,435,3,10,104,0,104,0,3,10,104,0,104,0,21102,709475849060,1,1,21102,1,423,0,1106,0,435,21102,868498428684,1,1,21101,434,0,0,1105,1,435,99,109,2,21201,-1,0,1,21101,0,40,2,21102,1,466,3,21101,456,0,0,1105,1,499,109,-2,2105,1,0,0,1,0,0,1,109,2,3,10,204,-1,1001,461,462,477,4,0,1001,461,1,461,108,4,461,10,1006,10,493,1101,0,0,461,109,-2,2106,0,0,0,109,4,2102,1,-1,498,1207,-3,0,10,1006,10,516,21102,1,0,-3,21201,-3,0,1,21201,-2,0,2,21102,1,1,3,21102,535,1,0,1106,0,540,109,-4,2106,0,0,109,5,1207,-3,1,10,1006,10,563,2207,-4,-2,10,1006,10,563,21202,-4,1,-4,1106,0,631,21201,-4,0,1,21201,-3,-1,2,21202,-2,2,3,21101,582,0,0,1105,1,540,22102,1,1,-4,21102,1,1,-1,2207,-4,-2,10,1006,10,601,21101,0,0,-1,22202,-2,-1,-2,2107,0,-3,10,1006,10,623,22102,1,-1,1,21101,623,0,0,105,1,498,21202,-2,-1,-2,22201,-4,-2,-4,109,-5,2105,1,0"
    val finalWhites = PaintingRobot(input).startDrawing(Colour.WHITE).whitePositions

    printCanvas(finalWhites)
}
