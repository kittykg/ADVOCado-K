import aoc2019.Moon
import aoc2019.NBodyProblem
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe


class NBodyProblemTest : FunSpec({
    test("kn 10") {
        val moons = listOf(
                Moon(-1, 0, 2),
                Moon(2, -10, -7),
                Moon(4, -8, 8),
                Moon(3, 5, -1)
        )
        NBodyProblem(moons).move(10) shouldBe 179
    }

    test("meet #1") {
        val moons = listOf(
                Moon(-1, 0, 2),
                Moon(2, -10, -7),
                Moon(4, -8, 8),
                Moon(3, 5, -1)
        )
        NBodyProblem(moons).meet() shouldBe 2772L
    }
})
