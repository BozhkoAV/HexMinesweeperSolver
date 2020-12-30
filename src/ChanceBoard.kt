class ChanceBoard (board: MutableMap<Pair<Int, Int>, Int>) {
    var chanceBoard = mutableMapOf<Pair<Int, Int>, SolverCell>()
    private var width = 0
    private var height = 0

    init {
        var maxFirst = 0
        var maxSecond = 0
        for (coordinate in board.keys) {
            if (coordinate.first + 1 > maxFirst) maxFirst = coordinate.first + 1
            if (coordinate.second + 1 > maxSecond) maxSecond = coordinate.second + 1
        }
        width = maxFirst
        height = maxSecond
    }

    fun updateChanceBoard(board: MutableMap<Pair<Int, Int>, Int>) {
        val result = mutableMapOf<Pair<Int, Int>, SolverCell>()
        var count = 0
        for (i in 0 until width) {
            for (j in 0 until height) {
                if (board[i to j] == -1) {
                    val cell = SolverCell(i to j, false)
                    result[i to j] = cell
                    count++
                } else {
                    if(board[i to j]!! >= 0) {
                        val solverCell = SolverCell(i to j, true)
                        solverCell.setValue(board[i to j]!!)
                        result[i to j] = solverCell
                    }
                }
            }
        }
        //println("Осталость $count")
        chanceBoard = result
        updateChances()
    }

    private fun updateChances() {
        updateSolverCells()
        setFlags()
        openNotBombsCells()
        findChance()
    }

    private fun updateSolverCells() {
        for (i in 0 until width) {
            for (j in 0 until height) {
                val cell = chanceBoard[i to j]!!
                val neighbors = getCoordinatesAround(i to j)
                cell.resetSurrounding()
                if (cell.getValue() != 0 && cell.isChecked()) {
                    for (around in neighbors) {
                        if (chanceBoard[around]!!.isFlag()) {
                            cell.flagsAround++
                        }
                        if (!chanceBoard[around]!!.isChecked()) {
                            cell.uncheckedNeighbours++
                        }
                    }
                }
            }
        }
    }

    private fun setFlags() {
        for (i in 0 until width) {
            for (j in 0 until height) {
                val cell = chanceBoard[i to j]!!
                val neighbors = getCoordinatesAround(i to j)
                if (cell.getValue() > 0 && cell.getValue() == cell.uncheckedNeighbours) {
                    for (around in neighbors) {
                        if (!chanceBoard[around]!!.isChecked()) {
                            chanceBoard[around]!!.setFlag()
                        }
                    }
                    updateSolverCells()
                }
            }
        }
    }

    private fun openNotBombsCells() {
        for (i in 0 until width) {
            for (j in 0 until height) {
                val cell = chanceBoard[i to j]!!
                val neighbors = getCoordinatesAround(i to j)
                if (cell.getValue() != 0 && cell.getValue() == cell.flagsAround) {
                    for (around in neighbors) {
                        if (!chanceBoard[around]!!.isChecked() && !chanceBoard[around]!!.isFlag()) {
                            chanceBoard[around]!!.setNotBomb()
                        }
                    }
                }
            }
        }
    }

    private fun findChance() {
        for (i in 0 until width) {
            for (j in 0 until height) {
                val cell = chanceBoard[i to j]!!
                val neighbors = getCoordinatesAround(i to j)
                if (cell.getValue() != 0 && cell.isChecked()) {
                    for (around in neighbors) {
                        if (!chanceBoard[around]!!.isChecked() && chanceBoard[around]!!.getChance() >= 0.0 &&
                            !chanceBoard[around]!!.isFlag()) {
                            val chance = (cell.getValue() - cell.flagsAround).toDouble() /
                                    (neighbors.size - cell.flagsAround).toDouble()
                            chanceBoard[around]!!.probabilityList.add(chance)
                            var probability = 1.0
                            for (element in chanceBoard[around]!!.probabilityList) {
                                probability *= (1.0 - element)
                            }
                            chanceBoard[around]!!.setChance(1.0 - probability)
                        }
                    }
                }
            }
        }
    }

    private fun isBelongBoard(coordinate: Pair<Int, Int>) =
        coordinate.first in 0 until width && coordinate.second in 0 until height

    private fun getCoordinatesAround(coordinate: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
        var around: Pair<Int, Int>
        val list = mutableListOf<Pair<Int, Int>>()
        var y = coordinate.second
        for (x in coordinate.first - 2..coordinate.first + 2) {
            around = x to y
            if ((isBelongBoard(around)) && (around != coordinate)) list.add(around)
        }
        y = if (coordinate.first % 2 == 0) y - 1 else y + 1
        for (x in coordinate.first - 1..coordinate.first + 1 step 2) {
            around = x to y
            if (isBelongBoard(around)) list.add(around)
        }
        return list
    }

    // для проверки
    fun print() {
        for ((coordinate, solverCell) in chanceBoard) {
            var flag = false
            if (solverCell.getChance() != 0.0) {
                println("$coordinate -> Шанс мины = ${solverCell.getChance()}")
                flag = true
            }
            if (solverCell.getValue() != 0) {
                println("$coordinate -> Цифра в ячейке = ${solverCell.getValue()}")
                flag = true
            }
            if (solverCell.isNotBomb()) {
                println("$coordinate -> Нет мины = ${solverCell.isNotBomb()}")
                flag = true
            }
            if (solverCell.isFlag()) {
                println("$coordinate -> Флаг = ${solverCell.isFlag()}")
                flag = true
            }
            if (solverCell.isChecked()) {
                println("$coordinate -> Проверено = ${solverCell.isChecked()}")
                flag = true
            }
            if (solverCell.getValue() == 0 && solverCell.isChecked()) {
                println("Соседи ${getCoordinatesAround(coordinate)}")
            }
            if (flag) println()
        }
    }

    // для проверки
    fun print(currentChanceBoard: MutableMap<Pair<Int, Int>, SolverCell>) {
        for ((coordinate, solverCell) in chanceBoard) {
            if (solverCell.getChance() != currentChanceBoard[coordinate]!!.getChance() ||
                solverCell.getValue() != currentChanceBoard[coordinate]!!.getValue() ||
                solverCell.isNotBomb() != currentChanceBoard[coordinate]!!.isNotBomb() ||
                solverCell.isFlag() != currentChanceBoard[coordinate]!!.isFlag() ||
                solverCell.isChecked() != currentChanceBoard[coordinate]!!.isChecked()) {
                val previousChance = String.format("%6.4f", currentChanceBoard[coordinate]!!.getChance())
                println("Было  $coordinate -> Шанс мины = $previousChance, " +
                        "Цифра в ячейке = ${currentChanceBoard[coordinate]!!.getValue()}, " +
                        "Нет мины = ${currentChanceBoard[coordinate]!!.isNotBomb()}, " +
                        "Флаг = ${currentChanceBoard[coordinate]!!.isFlag()}, " +
                        "Проверено = ${currentChanceBoard[coordinate]!!.isChecked()}, "
                )
                val chance = String.format("%6.4f", solverCell.getChance())
                println("Стало $coordinate -> Шанс мины = $chance, " +
                        "Цифра в ячейке = ${solverCell.getValue()}, " +
                        "Нет мины = ${solverCell.isNotBomb()}, " +
                        "Флаг = ${solverCell.isFlag()}, " +
                        "Проверено = ${solverCell.isChecked()}, "
                )
                println()
            }
        }
    }

    // для проверки
    fun getCurrentChanceBoard() = chanceBoard

    // для оптимизации
    fun getListOfFlag(): MutableList<Pair<Int, Int>> {
        val flags = mutableListOf<Pair<Int, Int>>()
        for((coordinate, cell) in chanceBoard) {
            if(cell.isFlag()) flags.add(coordinate)
        }
        return flags
    }
}