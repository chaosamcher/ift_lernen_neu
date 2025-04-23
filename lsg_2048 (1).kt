import kotlin.random.Random.Default.nextInt

fun repeat(s: String, count: Int): String {
    var acc = ""
    for (i in 1..count) {
        acc += s
    }
    return acc
}

fun showNumber(number: Int): String = "|" + repeat(" ", 7 - number.toString().length) + number.toString()


fun showRow(xs: List<Int>): String {
    var acc = ""
    for (x in xs) {
        acc += showNumber(x) + " "
    }
    return acc + "|"
}


fun showBoard(board: List<List<Int>>): List<String> {
    var acc = emptyList<String>()
    for (row in board) {
        acc += showRow(row)
    }
    return acc
}


fun printStrings(xs:List<String>) {
    for (row in xs) {
        println(row)
    }
}


fun printBoard(board: List<List<Int>>) {
    for (row in showBoard(board)) {
        println(row)
    }
}

fun removeZeros(row: List<Int>): List<Int> {
    var acc = emptyList<Int>()
    for (x in row) {
        acc = if (x != 0) acc + x else acc
    }
    return acc
}

fun <T> fill(row: List<T>, elem: T, desiredSize: Int): List<T> {
    var acc = row
    for (x in row.size + 1..desiredSize) {
        acc = acc + elem
    }
    return acc
}






fun moveRowLeftHelper(row: List<Int>): List<Int> {
    var acc = emptyList<Int>()
    var i = 0
    while (i in 0..row.size - 2) {
        if (row[i] == row[i + 1]) {
            acc = acc + 2 * row[i]
            i += 2
        } else {
            acc = acc + row[i]
            i++
        }
    }
    return if (i == row.size - 1) acc + row[i] else acc
}


fun moveRowLeft(xs: List<Int>): List<Int> = fill(moveRowLeftHelper(removeZeros(xs)), 0, xs.size)

fun moveBoardLeft(xs: List<List<Int>>): List<List<Int>> {
    var acc = emptyList<List<Int>>()
    for (x in xs) {
        acc = acc + listOf(moveRowLeft(x))
    }
    return acc
}



fun getColumn(board: List<List<Int>>, columnIndex: Int): List<Int> {
    var acc = emptyList<Int>()
    for (row in board) {
        acc = acc + row[columnIndex]
    }
    return acc
}

fun reverse(xs: List<Int>): List<Int> {
    var acc = emptyList<Int>()
    for (i in 0..xs.size - 1) {
        acc = acc + xs[xs.size - 1 - i]
    }
    return acc
}

fun rotateRight(board: List<List<Int>>): List<List<Int>> {
    var acc = emptyList<List<Int>>()
    for (columnIndex in 0..board[0].size - 1) {
        acc += listOf(reverse(getColumn(board, columnIndex)))
    }
    return acc
}

fun rotateRight(xs: List<List<Int>>, count: Int): List<List<Int>> {
    var acc = xs
    for (i in 1..count) {
        acc = rotateRight(acc)
    }
    return acc
}


fun rotateMoveLeftRotateBack(board: List<List<Int>>, count: Int): List<List<Int>> =
    rotateRight(moveBoardLeft(rotateRight(board, count)), 4 - count)


fun computeNecessaryRotationsBeforeMove(c: Char): Int = when (c) {
    's' -> 1
    'd' -> 2
    'w' -> 3
    else -> 0
}


fun move(board: List<List<Int>>, c: Char) = rotateMoveLeftRotateBack(board, computeNecessaryRotationsBeforeMove(c))



fun checkMovesPossible(xss: List<List<Int>>): Boolean {
    var acc = false
    for (dir in 0..3) {
        acc = acc || rotateMoveLeftRotateBack(
            xss, dir
        ) != xss
    }
    return (acc)
}


// -------------------------------------------------------------
/// Random Tile Generator
// -------------------------------------------------------------


fun contains(xs: List<Int>, number: Int): Boolean {
    for (x in xs) {
        if (x == number) return true

    }
    return false
}


fun contains2D(xss: List<List<Int>>, number: Int): Boolean {
    for (row in xss) {
        if (contains(row, number)) return true
    }
    return false
}


fun <T> setAt(xs: List<T>, index: Int, elem: T): List<T> {
    var acc = listOf<T>()
    for (i in 0..xs.size - 1) {
        acc = acc + if (i == index) elem else xs[i]
    }
    return acc
}


fun <T> setAt(xs: List<List<T>>, rowIndex: Int, columnIndex: Int, elem: T): List<List<T>> =
    setAt(xs, rowIndex, setAt(xs[rowIndex], columnIndex, elem))


fun <T> randomIndex(xs: List<T>) = nextInt(xs.size)


fun generateTwoOrFour(): Int {
    val r = nextInt(10)
    return if (r == 0) 4 else 2
}

fun addTwoOrFour(xss: List<List<Int>>): List<List<Int>> {
    if (!contains2D(xss, 0)) return xss
    var rowIndex = randomIndex(xss)
    var colIndex = randomIndex(xss[0])
    while (xss[rowIndex][colIndex] != 0) {
        rowIndex = randomIndex(xss)
        colIndex = randomIndex(xss[0])
    }
    val numberToSet = generateTwoOrFour()
    return setAt(xss, rowIndex, colIndex, numberToSet)
}


// -------------------------------------------------------------
/// Update
// -------------------------------------------------------------



fun update(board: List<List<Int>>, c: Char): List<List<Int>> =
    if (!listOf('w', 'a', 's', 'd').contains(c) || move(board, c) == board) board else addTwoOrFour(move(board, c))

// -------------------------------------------------------------
/// Ergebnis anzeigen
// -------------------------------------------------------------

fun showResult(won: Boolean): String = "Du hast " + if (won) "gewonnen" else "verloren"

fun <T> repeat2D(rows: Int, cols: Int, elem: T) = fill(listOf(), fill(listOf(), elem, cols), rows)

fun createStartBoard(rows: Int, cols: Int) = addTwoOrFour(addTwoOrFour(repeat2D(rows, cols, 0)))



fun readChar() = readln()[0]


fun play2048(rows: Int, cols: Int) {
    var gameBoard = createStartBoard(rows, cols)
    var gameWon = false
    var pressedExit = false
    while (checkMovesPossible(gameBoard) && !pressedExit) {
//        println(repeat("\n", 50))
        printBoard(gameBoard)
        val input = readChar()
        pressedExit = input == 'q'
        gameBoard = update(gameBoard, input)
        gameWon = gameWon || contains2D(gameBoard, 2048)
    }
    println(showResult(gameWon))
}

fun main(){
}
