package tictactoe

import java.util.Scanner
import kotlin.math.abs

var counter = 0

fun main() {
    val scanner = Scanner(System.`in`)
    var gameZone = "_________"

    drawGameZone(gameZone)

    while (!isGameEnded(gameZone)) {
        gameZone = gameProcess(gameZone, scanner)
        drawGameZone(gameZone)
    }

    if (isGameEnded(gameZone)) println(analyzeGameZone(gameZone))
}

fun drawGameZone(zone : String) {
    var y = 0
    var x = 0
    while (y < 5) {
        if (y == 0 || y == 4) {
            println("---------")
            y++
        }
        while (x < 8) {
            println("| ${zone[x]} ${zone[x + 1]} ${zone[x + 2]} |")
            y++
            x += 3
        }
    }
}

fun gameProcess(zone : String, sc : Scanner): String {
    var gameZone = zone
//    var coordinates = ""
    do {
        val sign: Char = if (counter == 0 || counter % 2 == 0) 'X' else 'O'
        var coordinates: String
        coordinates = getCoords(sc)
        if (coordinates != "") coordinates = verifyCoords(coordinates)
        if (coordinates == "") continue
        if (!isCellOccupied(zone,coordinates) && coordinates != "") {
            gameZone = putSign(gameZone, coordinates, sign)
            ++counter
        }
        else {
            coordinates = ""
            println("This cell is occupied! Choose another one!")
        }

    } while (coordinates == "")
    return gameZone
}

fun isGameEnded(zone : String): Boolean {
    return Regex("""([X|O] wins)|(Draw)""").matches(analyzeGameZone(zone))
}

fun getCoords(sc : Scanner) : String {
    val returnValue: String
    print("Enter the coordinates: ")
    val input = sc.nextLine()
    returnValue = if (Regex("""\d\s\d""").matches(input)) {
        input.substringBefore(' ') + input.substringAfter(' ')
    } else {
        println("You should enter numbers!")
        ""
    }
    return returnValue
}

fun verifyCoords(coords : String) : String {
    var returnValue = coords
    if (coords[0].toString().toInt() !in 1..3 || coords[1].toString().toInt() !in 1..3) {
        println("Coordinates should be from 1 to 3!")
        returnValue = ""
    }
    return returnValue
}

fun putSign(zone : String, coords : String, sign : Char): String {
    val pos = getPositionOfCoords(coords)
    return if (pos!! != 0) zone.substring(0, pos) + sign.toString() + zone.substring(pos+1, zone.length)
    else sign.toString() + zone.substring(pos + 1, zone.length)
}

fun isCellOccupied(zone : String, coords : String) : Boolean {
    val pos = getPositionOfCoords(coords)
    return zone[pos!!] != '_'
}

fun getPositionOfCoords(coords : String) : Int? {
    return when (coords[0]) {
        '1' -> coords[1].toString().toInt() - 1
        '2' -> coords[0].toString().toInt() + coords[1].toString().toInt()
        '3' -> 2 + coords[0].toString().toInt() + coords[1].toString().toInt()
        else -> null
    }
}

fun analyzeGameZone(zone : String) : String {
    var returnState: String
    returnState = winStateAnalyzer(zone)
    if (xoDifference(zone) || bothWin(zone)) returnState = "Impossible"
    else if (zone.contains('_') && returnState == "") returnState = "Game not finished"
    return returnState
}

fun winStateAnalyzer(zone : String) : String {
    var returnState = ""
    label@ for (i in 0..8) {
        when(i) {
            0 -> {
                for(index in 0..2) {
                    if (rowWinState(zone, index) != "default") { returnState = rowWinState(zone, index); break@label }
                    else if (columnWinState(zone, index) != "default") { returnState = columnWinState(zone, index); break@label }
                }
                if(mainDiagonalWinState(zone) != "default") { returnState = mainDiagonalWinState(zone); break@label }
            }
            1 -> { if (columnWinState(zone, i) != "default") { returnState = columnWinState(zone,i); break@label } }
            2 -> { if (otherDiagonalWinState(zone) != "default") { returnState = otherDiagonalWinState(zone); break@label } }
            3 -> { if (rowWinState(zone, i) != "default") { returnState = rowWinState(zone,i); break@label } }
            6 -> { if (rowWinState(zone, i) != "default") { returnState = rowWinState(zone,i); break@label } }
            else -> if (drawStateCheck(zone)) returnState = "Draw"
        }
    }
    return returnState
}

fun xoDifference(zone : String): Boolean {
    val amountX = zone.filter { it == 'X' }.count()
    val amountO = zone.filter { it == 'O' }.count()
    return !( abs(amountX - amountO) == 0 || abs(amountX - amountO) == 1 )
}

fun bothWin(zone : String): Boolean {
    var xWinState = false
    for (i in 0..2) {
        if (rowWinState(zone, i) == "X wins" || columnWinState(zone, i) == "X wins") { xWinState = true; break }
    }
    var oWinState = false
    for (i in 0..2) {
        if (rowWinState(zone, i) == "O wins" || columnWinState(zone, i) == "O wins") { oWinState = true; break }
    }
    return xWinState && oWinState
}

fun rowWinState(zone : String, row: Int): String {
    var returnState = "default"
    if (zone[row] == zone[row + 1] && zone[row] == zone[row + 2]) {
        returnState = if (zone[row] == 'X') "X wins" else if (zone[row] == 'O') "O wins" else "default"
    }
    return returnState
}

fun columnWinState(zone : String, column: Int): String  {
    var returnState = "default"
    if (zone[column] == zone[column + 3] && zone[column] == zone[column + 6]) {
        returnState = if (zone[column] == 'X') "X wins" else if (zone[column] == 'O') "O wins" else "default"
    }
    return returnState
}

fun mainDiagonalWinState(zone : String): String  {
    var returnState = "default"
    val x = 0
    if (zone[x] == zone[x+4] && zone[x] == zone[x+8]) {
        returnState = if (zone[x] == 'X') "X wins" else if (zone[x] == 'O') "O wins" else "default"
    }
    return returnState
}

fun otherDiagonalWinState(zone : String): String  {
    var returnState = "default"
    val x = 2
    if (zone[x] == zone[x+2] && zone[x] == zone[x+4]) {
        returnState = when {
            zone[x] == 'X' -> "X wins"
            zone[x] == 'O' -> "O wins"
            else -> "default"
        }
    }
    return returnState
}

fun drawStateCheck(zone : String) : Boolean {
    var returnBool = false
    if (!zone.contains('_')) returnBool = true
    return returnBool
}