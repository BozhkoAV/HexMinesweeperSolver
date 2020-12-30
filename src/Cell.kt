class Cell {
    private var value = 0
    private var bomb = false
    private var flag = false
    var check = false

    fun isBomb() = bomb

    fun placeBomb() {
        bomb = true
        value = -1
    }

    fun placeFlag() {
        flag = true
    }

    fun setValue(newValue: Int) {
        value = newValue
    }

    fun getValue() = value
}