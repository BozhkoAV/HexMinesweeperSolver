class Board (width: Int, height: Int, bombs: Int) {
    val gameBoard = mutableMapOf<Pair<Int, Int>, Cell>()
    private val boardWidth = width
    private val boardHeight = height
    private var emptyCells = width * height - bombs
    private var checkCells = 0
    private val bombsList = mutableListOf<Pair<Int, Int>>() // для проверки
    var state = "playing"

    init {
        generateBoard(width, height, bombs)
    }

    private fun getWidth() = boardWidth

    private fun getHeight() = boardHeight

    private fun generateBoard(width: Int, height: Int, bombs: Int) {
        require(bombs <= width * height) { "Слишком много мин для данного поля" }
        for (i in 0 until width) {
            for (j in 0 until height) {
                gameBoard[i to j] = Cell()
            }
        }
        for (i in 0 until bombs) {
            var randomCoordinate = (0 until width).random() to (0 until height).random()
            while (gameBoard[randomCoordinate]!!.isBomb()) {
                randomCoordinate = (0 until width).random() to (0 until height).random()
            }
            gameBoard[randomCoordinate]!!.placeBomb()
            bombsList.add(randomCoordinate) // для проверки
        }
        println("Список мин: $bombsList") // для проверки
        updateValues()
    }

    private fun updateValues() {
        for ((coordinate, cell) in gameBoard) {
            if (cell.isBomb()) {
                val neighbors = getCoordinatesAround(coordinate)
                for (around in neighbors) {
                    val currentNeighbor = gameBoard[around]!!
                    if (!currentNeighbor.isBomb()) {
                        currentNeighbor.setValue(currentNeighbor.getValue() + 1)
                    }
                }
            }
        }
    }

    fun uncover(coordinate: Pair<Int, Int>) {
        gameBoard[coordinate]!!.check = true
        if (gameBoard[coordinate]!!.isBomb()) {
            state = "lose"
            return
        } else {
            checkCells++
            if (gameBoard[coordinate]!!.getValue() == 0) {
                val neighbors = getCoordinatesAround(coordinate)
                for (around in neighbors) {
                    if (!gameBoard[around]!!.isBomb() && !gameBoard[around]!!.check) {
                        uncover(around)
                    }
                }
            }
        }
        if (checkCells == emptyCells) {
            state = "win"
        }
    }

    fun getBoard(): MutableMap<Pair<Int, Int>, Int> {
        val width = getWidth()
        val height = getHeight()
        val board = mutableMapOf<Pair<Int, Int>, Int>()
        for (i in 0 until width) {
            for (j in 0 until height) {
                if (gameBoard[i to j]!!.check) {
                    board[i to j] = gameBoard[i to j]!!.getValue()
                } else {
                    board[i to j] = -1
                }
            }
        }
        return board
    }

    private fun isBelongBoard(coordinate: Pair<Int, Int>) =
        coordinate.first in 0 until boardWidth && coordinate.second in 0 until boardHeight

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
}