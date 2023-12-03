fun Char.isSymbol(): Boolean {
    return !this.isDigit() && this != '.'
}

fun IntArray.getIfNotZero(index: Int): Int? {
    return if (getOrNull(index) != 0 && getOrNull(index) != null) {
        getOrNull(index)
    } else {
        null
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val array = input.map { it.toCharArray() }.toTypedArray()
        return input.mapIndexed { lineNumber, lineText ->
           "[0-9]+".toRegex().findAll(lineText).sumOf {
               val indexStart = it.range.first()
               val indexEnd = it.range.last()
               var foundValidNumber = false
               (indexStart-1..indexEnd+1).forEach { index ->
                   if (
                       array.getOrNull(lineNumber - 1)?.getOrNull(index)?.isSymbol() == true ||
                       array.getOrNull(lineNumber)?.getOrNull(index)?.isSymbol() == true ||
                       array.getOrNull(lineNumber + 1)?.getOrNull(index)?.isSymbol() == true
                   ) {
                       foundValidNumber = true
                   }
               }
               if (foundValidNumber) {
                   it.value.toInt()
               } else {
                   0
               }
            }
        }.sum()
    }

    fun part2(input: List<String>): Int {
        val numberArray: Array<IntArray> = Array(input.size) { IntArray(input[0].length) {0} }
        input.forEachIndexed { lineNumber, lineText ->
            "[0-9]+".toRegex().findAll(lineText).forEach { match ->
                (match.range.first..match.range.last).forEach { index ->
                    numberArray[lineNumber][index] = match.value.toInt()
                }
            }
        }

        return input.mapIndexed { x, line ->
            val symbolsInLine = "[^A-Z0-9.]".toRegex().findAll(line)
            val yCoordinatesOfSymbols = symbolsInLine.map {
                it.range.first()
            }
            yCoordinatesOfSymbols.sumOf { y ->
                val adjacentNumbers: MutableList<Int> = mutableListOf()

                numberArray.getOrNull(x - 1)?.getIfNotZero(y - 1)?.let {
                    adjacentNumbers.add(it)
                }
                numberArray.getOrNull(x - 1)?.getIfNotZero(y)?.let {
                    adjacentNumbers.add(it)
                }
                numberArray.getOrNull(x - 1)?.getIfNotZero(y + 1)?.let {
                    adjacentNumbers.add(it)
                }
                numberArray.getOrNull(x)?.getIfNotZero(y - 1)?.let {
                    adjacentNumbers.add(it)
                }
                numberArray.getOrNull(x)?.getIfNotZero(y + 1)?.let {
                    adjacentNumbers.add(it)
                }
                numberArray.getOrNull(x + 1)?.getIfNotZero(y - 1)?.let {
                    adjacentNumbers.add(it)
                }
                numberArray.getOrNull(x + 1)?.getIfNotZero(y)?.let {
                    adjacentNumbers.add(it)
                }
                numberArray.getOrNull(x + 1)?.getIfNotZero(y + 1)?.let {
                    adjacentNumbers.add(it)
                }

                val distinct = adjacentNumbers.distinct()
                val adjacentNumberCount = distinct.size
                when {
                    adjacentNumberCount <= 1 -> 0
                    adjacentNumberCount == 2 -> {
                        distinct.first() * distinct.last()
                    }
                    adjacentNumberCount > 2 -> error("not allowed to have more than 2")
                    else -> error("should never happen")
                }
            }
        }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}