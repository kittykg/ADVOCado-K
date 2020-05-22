package aoc2019

fun List<Int>.permutations(): Set<List<Int>> = when {
    isEmpty() -> setOf()
    size == 1 -> setOf(this)
    else -> {
        val elementList = listOf(get(0))
        drop(1).permutations().flatMap { p ->
            (0..p.size).map { i -> p.subList(0, i) + elementList + p.subList(i, p.size) }
        }.toSet()
    }
}

object TrialMachine {
    private fun trial(input: String, phases: List<Int>): Int =
            phases.fold(0) { acc: Int, i: Int ->
                FunkyPuter.runAndShowLastOutput(input, listOf(i, acc))
            }

    fun findMaxThruster(input: String) =
            (0..4).toList().permutations().toList().map { trial(input, it) }.max()

    private tailrec fun loop(states: List<State>): Int {
        if (states.all { it.isTerminated }) return states.last().output.last()

        val newStates = states.indices.fold(states) { acc: List<State>, i: Int ->
            val numAmp = states.size
            val ampLastState = acc[i]
            val prevAmpState = acc[(i - 1 + numAmp) % numAmp]

            val newInputCodes = ampLastState.inputCodes + (if (prevAmpState.output.isEmpty())
                listOf() else listOf(prevAmpState.output.last()))
            val newState = State(ampLastState.machine, ampLastState.pointer,
                    newInputCodes, ampLastState.output, ioBlocked = false, isTerminated = false)
            acc.mapIndexed { index: Int, s: State ->
                if (index == i) FunkyPuter.runOnState(newState) else s
            }
        }
        return loop(newStates)
    }

    fun loopMaxThruster(input: String): Int? {
        val initMachine = input.split(",").map { it.toInt() }

        return (5..9).toList().permutations().toList().map { phases ->
            val initStates = phases.mapIndexed { index: Int, i: Int ->
                val inputCodes = if (index == 0) listOf(i, 0) else listOf(i)
                State(initMachine, 0, inputCodes, listOf(),
                        ioBlocked = false, isTerminated = false)
            }
            loop(initStates)
        }.max()
    }
}

fun main() {
    val input = "3,8,1001,8,10,8,105,1,0,0,21,30,39,64,81,102,183,264,345,426,99999,3,9,1001,9,2,9,4,9,99,3,9,1002,9,4,9,4,9,99,3,9,1002,9,5,9,101,2,9,9,102,3,9,9,1001,9,2,9,1002,9,2,9,4,9,99,3,9,1002,9,3,9,1001,9,5,9,1002,9,3,9,4,9,99,3,9,102,4,9,9,1001,9,3,9,102,4,9,9,1001,9,5,9,4,9,99,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,1,9,9,4,9,3,9,101,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,1,9,4,9,99,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,101,1,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,1001,9,1,9,4,9,3,9,102,2,9,9,4,9,99,3,9,101,1,9,9,4,9,3,9,101,2,9,9,4,9,3,9,101,1,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,99,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,2,9,4,9,99,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,1001,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,101,1,9,9,4,9,99"
    println(TrialMachine.findMaxThruster(input))
    println(TrialMachine.loopMaxThruster(input))
}
