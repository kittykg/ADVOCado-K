package aoc2017

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PacketScannerTest : FunSpec({
    test("example 1") {
        val input = """
            0: 3
            1: 2
            4: 4
            6: 4
        """.trimIndent()
        PacketScanner(input).getTotalImpact() shouldBe 24
    }

    test("example 2") {
        val input = """
            0: 3
            1: 2
            4: 4
            6: 4
        """.trimIndent()
        PacketScanner(input).safePass() shouldBe 10
    }
})
