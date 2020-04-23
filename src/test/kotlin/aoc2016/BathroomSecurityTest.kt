import aoc2016.BathroomSecurity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BathroomSecurityTest : FunSpec({
    test("example 1.1") {
        val input = """
            ULL
            RRDDD
            LURDL
            UUUUD
        """.trimIndent()
        BathroomSecurity(input).getCode() shouldBe "1985"
    }

    test("example 2.1") {
        val input = """
            ULL
            RRDDD
            LURDL
            UUUUD
        """.trimIndent()
        BathroomSecurity(input).getFunkyCode() shouldBe "5DB3"
    }
})
