import org.junit.jupiter.api.Test

class SolverTest {
    @Test
    fun solver() {
        for (i in 1..10) {
            val game = Solver(10, 10, 10)
            game.start()
        }
    }
}