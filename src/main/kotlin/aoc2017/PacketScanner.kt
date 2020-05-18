package aoc2017

data class LayerConfig(val depth: Int, val range: Int)

class PacketScanner(private val input: String) {
    private val layerConfigs = input.split("\n").map { splitNum(it) }

    private fun splitNum(input: String): LayerConfig {
        val numbers = input.split(": ").map { it.toIntOrNull() }
        if (numbers.contains(null) || numbers.size != 2) {
            throw UnsupportedOperationException("Bad input bro")
        } else return LayerConfig(numbers[0]!!, numbers[1]!!)
    }

    private fun getSingleImpact(layerConfig: LayerConfig): Int {
        return if (layerConfig.depth % (2 * layerConfig.range - 2) == 0)
            layerConfig.depth * layerConfig.range else 0
    }

    private fun safe(layerConfig: LayerConfig, delay: Int): Boolean =
            (layerConfig.depth + delay) % (2 * layerConfig.range - 2) != 0

    fun getTotalImpact(): Int = layerConfigs.map { getSingleImpact(it) }.sum()

    private tailrec fun safePassHelper(delay: Int): Int {
        return if (layerConfigs.fold(true) { acc, lc -> acc && safe(lc, delay) })
            delay else safePassHelper(delay + 1)
    }

    fun safePass(): Int = safePassHelper(0)
}

fun main() {
    val input = """
        0: 4
        1: 2
        2: 3
        4: 4
        6: 8
        8: 5
        10: 8
        12: 6
        14: 6
        16: 8
        18: 6
        20: 6
        22: 12
        24: 12
        26: 10
        28: 8
        30: 12
        32: 8
        34: 12
        36: 9
        38: 12
        40: 8
        42: 12
        44: 17
        46: 14
        48: 12
        50: 10
        52: 20
        54: 12
        56: 14
        58: 14
        60: 14
        62: 12
        64: 14
        66: 14
        68: 14
        70: 14
        72: 12
        74: 14
        76: 14
        80: 14
        84: 18
        88: 14
    """.trimIndent()
    val ps = PacketScanner(input)
    println(ps.getTotalImpact())
    println(ps.safePass())
}
