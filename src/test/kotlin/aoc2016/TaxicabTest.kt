import aoc2016.Taxicab
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TaxicabTest : FunSpec({
    test("example 1.1") {
        val input = "R2, L3"
        Taxicab(input).move() shouldBe 5
    }

    test("example 1.2") {
        val input = "R2, R2, R2"
        Taxicab(input).move() shouldBe 2
    }

    test("example 1.3") {
        val input = "R5, L5, R5, R3"
        Taxicab(input).move() shouldBe 12
    }

    test("example 2.1") {
        val input = "R8, R4, R4, R8"
        Taxicab(input).check() shouldBe 4
    }
})
