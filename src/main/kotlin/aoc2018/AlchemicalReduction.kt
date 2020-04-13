package aoc2018

class AlchemicalReduction {
    private fun reactConditionSatisfied(c1: Char, c2: Char): Boolean {
        return kotlin.math.abs(c1 - c2) == 32
    }

    private fun checkBase(c: Char, base: Char): Boolean {
        val diff = kotlin.math.abs(c - base)
        return diff == 0 || diff == 32
    }

    fun react(polymer: String): Int {
        val sb = StringBuilder(polymer)
        while (true) {
            var p = sb.toString()
            var len = sb.length
            for (i in 0 until len - 1) {
                val c1 = p[i]
                val c2 = p[i + 1]
                if (reactConditionSatisfied(c1, c2)) {
                    sb.deleteCharAt(i)
                    sb.deleteCharAt(i)
                    break
                }
            }
            if (sb.length == len) break
        }
        return sb.toString().length
    }

    fun deleteAndReact(polymer: String): Int {
        val map = mutableMapOf<Char, Int>()
        for (base in 'a'..'z') {
            val filtered = polymer.filter { !checkBase(it, base) }
            map[base] = react(filtered)
        }
        return map.minBy { it.value }!!.value
    }

    tailrec fun funcReact(polymer: String): String {
        val newPolymer = polymer.fold("") { acc, ele ->
            if (acc.isNotEmpty() && reactConditionSatisfied(
                    acc.last(),
                    ele
                )
            ) acc.dropLast(1)
            else acc + ele
        }
        if (newPolymer.length == polymer.length) return newPolymer
        return funcReact(newPolymer)
    }
}

tailrec fun sth(i: Int, n: Int, res: Int = 1): Int {
    if (n == 1) {
        return res
    }
    return sth(i, n - 1, res * i)
}

fun main() {
    println(AlchemicalReduction().funcReact("dabAcCaCBAcCcaDA") == "dabCBAcaDA")
}