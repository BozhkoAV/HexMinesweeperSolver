class Solver (width: Int, height: Int, bombs: Int) {
    private val board = Board(width, height, bombs)
    private val boardWidth = width
    private val boardHeight = height
    private val boardBombs = bombs
    private var firstMove = true // первый ход или нет
    private val chanceBoard = ChanceBoard(board.getBoard())
    private lateinit var testChanceBoard: MutableMap<Pair<Int, Int>, SolverCell>

    fun start() {
        while (board.state == "playing") {
            play()
            if (board.state == "lose") {
                println("Поражение")
                println()
            }
            if (board.state == "win") {
                println("Победа")
                println()
            }
        }
    }

    private fun play() {
        if (firstMove) {
            randomMove()
            firstMove = false
            return
        } else {
            move(bestMoveCoordinate())
        }
    }

    private fun bestMoveCoordinate() : Pair<Int, Int> {
        var minimumChance = Double.MAX_VALUE
        var coordinate = 0 to 0
        for (i in 0 until boardWidth) {
            for (j in 0 until boardHeight) {
                val solverCell = chanceBoard.chanceBoard[i to j]!!
                if (solverCell.getChance() >= -0.01 && solverCell.getChance() <= minimumChance &&
                    !solverCell.isFlag() && !solverCell.isChecked()) {
                    coordinate = solverCell.getCoordinate()
                    minimumChance = solverCell.getChance()
                }
            }
        }
        return coordinate
    }

    private fun move(currentCoordinate: Pair<Int, Int>): Boolean {
        if (board.gameBoard[currentCoordinate]!!.check || board.gameBoard[currentCoordinate]!!.isFlag()) {
            return false
        }
        println("Ход: $currentCoordinate") // Для проверки
        board.openCells(currentCoordinate)
        val list = chanceBoard.updateChanceBoard(board.getBoard())
        //println("Флаги ${chanceBoard.getListOfFlag()}")  // Для проверки
        if (chanceBoard.getListOfFlag().size == boardBombs) {
            board.state = "win"
        }
        for (coordinate in list) {
            board.gameBoard[coordinate]!!.placeFlag()
        }
        if (firstMove) {
            //chanceBoard.print() // Для проверки
            testChanceBoard = chanceBoard.getCurrentChanceBoard() // Для проверки
        } else {
            //chanceBoard.print(testChanceBoard) // Для проверки
            testChanceBoard = chanceBoard.getCurrentChanceBoard() // Для проверки
        }
        return true
    }

    private fun randomMove() = move((0 until boardWidth).random() to (0 until boardHeight).random())
}