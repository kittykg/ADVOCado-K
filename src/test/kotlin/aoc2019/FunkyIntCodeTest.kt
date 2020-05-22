package aoc2019

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FunkyIntCodeTest : FunSpec({
    test("example 2") {
        val input = "3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99"
        FunkyPuter.runAndShowLastOutput(input, listOf(1)) shouldBe 999
        FunkyPuter.runAndShowLastOutput(input, listOf(8)) shouldBe 1000
        FunkyPuter.runAndShowLastOutput(input, listOf(9)) shouldBe 1001
    }
})
