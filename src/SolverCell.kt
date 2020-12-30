class SolverCell (coordinate: Pair<Int, Int>, private var check: Boolean) {
    private val solverCellCoordinate = coordinate
    private var value = 0
    private var notBomb = false
    private var flag = false
    var uncheckedNeighbours = 0
    var flagsAround = 0
    private var chance = 0.0
    val probabilityList = mutableListOf<Double>()

    fun getCoordinate() = solverCellCoordinate

    fun setValue(_value: Int) {
        value = _value
    }

    fun getValue() = value

    fun setChance(_chance: Double) {
        chance = _chance
    }

    fun getChance() = chance

    fun isChecked() = check

    fun setFlag() {
        chance = 1.0
        flag = true
    }

    fun isNotBomb() = notBomb

    fun isFlag() = flag

    fun resetSurrounding() {
        uncheckedNeighbours = 0
        flagsAround = 0
    }

    fun setNotBomb() {
        chance = -0.01
        notBomb = true
    }
}