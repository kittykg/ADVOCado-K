package aoc2019

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ProgramAlarmTest: FunSpec ({
    test("example 1.1") {
        val input = "1,9,10,3,2,3,11,0,99,30,40,50"
        IntCodeMachine(input).run()[0] shouldBe 3500
    }

    test("example 1.2") {
        val input = "1,0,0,0,99"
        IntCodeMachine(input).run() shouldBe listOf(2, 0, 0, 0, 99)
    }
})
