import aoc2018.AlchemicalReduction
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe


class AlchemicalReductionTest : FunSpec({
    val ar = AlchemicalReduction()

    test("react") {
        ar.funcReact("aa") shouldBe "aa"
        ar.funcReact("Aa") shouldBe ""
    }
})