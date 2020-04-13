package aoc2019

fun velChange(x: Int, y: Int) = if (x > y) -1 else if (x == y) 0 else 1

tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

fun lcm(a: List<Long>): Long = a.reduce { acc, i -> acc * i / gcd(acc, i) }

class Moon(val initX: Int, val initY: Int, val initZ: Int) {
    var x = initX
    var y = initY
    var z = initZ

    var velX = 0
    var velY = 0
    var velZ = 0

    fun applyGrav(moons: List<Moon>) {
        velX += moons.map { velChange(x, it.x) }.sum()
        velY += moons.map { velChange(y, it.y) }.sum()
        velZ += moons.map { velChange(z, it.z) }.sum()
    }

    fun applyVel() {
        x += velX
        y += velY
        z += velZ
    }

    fun calKN(): Int {
        fun absSum(a: Int, b: Int, c: Int) = kotlin.math.abs(a) + kotlin.math.abs(b) + kotlin.math.abs(c)
        return absSum(x, y, z) * absSum(velX, velY, velZ)
    }
}

class NBodyProblem(private val moons: List<Moon>) {
    val numMoons = moons.size

    val initXs = moons.map { it.initX }
    val initYs = moons.map { it.initY }
    val initZs = moons.map { it.initZ }

    tailrec fun move(step: Long): Int {
        if (step == 0L) return moons.sumBy { it.calKN() }
        moons.forEach { it.applyGrav(moons) }
        moons.forEach { it.applyVel() }
        return move(step - 1)
    }

    tailrec fun cycle(lastMoons: Pair<List<Int>, List<Int>>,
                      states: HashMap<List<Int>, List<List<Int>>>, steps: Long): Long {
        val xs = lastMoons.first
        val vs = lastMoons.second
        val accs = xs.map { i -> xs.map { velChange(i, it) }.sum() }
        val newVs = vs.zip(accs) { v, a -> v + a }
        val newXs = xs.zip(newVs) { x, v -> x + v }

        val existVs = states[newXs]
        if (existVs != null && states[newXs]!!.contains(newVs)) return steps + 1
        states[newXs] = if (existVs != null) existVs + listOf(newVs) else listOf(newVs)
        return cycle(Pair(newXs, newVs), states, steps + 1)
    }

    fun xCycle(): Long {
        val initVel = List(numMoons) { 0 }
        val initState = hashMapOf<List<Int>, List<List<Int>>>(initXs to listOf(initVel))
        return cycle(Pair(initXs, initVel), initState, 0L)
    }

    fun yCycle(): Long {
        val initVel = List(numMoons) { 0 }
        val initState = hashMapOf<List<Int>, List<List<Int>>>(initYs to listOf(initVel))
        return cycle(Pair(initYs, initVel), initState, 0L)
    }

    fun zCycle(): Long {
        val initVel = List(numMoons) { 0 }
        val initState = hashMapOf<List<Int>, List<List<Int>>>(initZs to listOf(initVel))
        return cycle(Pair(initZs, initVel), initState, 0L)
    }

    fun meet(): Long {
        val x = xCycle()
        val y = yCycle()
        val z = zCycle()
        val cycles = listOf(x, y, z)
        return lcm(cycles)
    }
}

fun main() {
    val moons = listOf(
            Moon(-2, 9, -5),
            Moon(16, 19, 9),
            Moon(0, 3, 6),
            Moon(11, 0, 11)
    )
    val nbp = NBodyProblem(moons)
    println(nbp.move(1000))
    println(nbp.meet())
}
