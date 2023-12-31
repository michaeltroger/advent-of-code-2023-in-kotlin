import kotlin.math.absoluteValue

enum class Pipe(private val char: Char) {
    VERTICAL('|'),
    HORIZONTAL('-'),
    NORTH_EAST('L'),
    NORTH_WEST('J'),
    SOUTH_WEST('7'),
    SOUTH_EAST('F'),
    STARTING_POSITION('S'),
    NO_PIPE('.');

    companion object {
        fun fromChar(char: Char) = entries.find { it.char == char }!!
    }

    override fun toString(): String {
        return when (this) {
            VERTICAL -> '│'
            HORIZONTAL -> '─'
            NORTH_EAST -> '└'
            NORTH_WEST -> '┘'
            SOUTH_WEST -> '┐'
            SOUTH_EAST -> '┌'
            NO_PIPE -> '.'
            STARTING_POSITION -> 'S'
        }.toString()
    }
}

fun main() {
    data class Coordinates(
        val x: Int,
        val y: Int,
    )

    data class Vertex(
        val coordinates: Coordinates,
        val data: Pipe,
    )

    fun addNeighborsToAdjacencyMap(adjacencyMap: MutableMap<Vertex, List<Vertex>>, vertex: Vertex, allVertices: Array<Array<Vertex>>) {
        val (x, y) = vertex.coordinates
        val neighbors = when (vertex.data) {
            Pipe.VERTICAL -> listOf(Coordinates(x - 1, y), Coordinates(x + 1, y))
            Pipe.HORIZONTAL -> listOf(Coordinates(x, y - 1), Coordinates(x, y + 1))
            Pipe.NORTH_EAST -> listOf(Coordinates(x - 1, y), Coordinates(x, y + 1))
            Pipe.NORTH_WEST -> listOf(Coordinates(x - 1, y), Coordinates(x, y - 1))
            Pipe.SOUTH_WEST -> listOf(Coordinates(x + 1, y), Coordinates(x, y - 1))
            Pipe.SOUTH_EAST -> listOf(Coordinates(x + 1, y), Coordinates(x, y + 1))
            Pipe.STARTING_POSITION -> listOf(Coordinates(-1, -1), Coordinates(-1, -1))
            Pipe.NO_PIPE -> listOf(Coordinates(-1, -1), Coordinates(-1, -1))
        }.mapNotNull {
            allVertices.getOrNull(it.x)?.getOrNull(it.y)
        }
        adjacencyMap[vertex] = neighbors
    }

    fun printAnimal(input: List<String>, animal: List<Vertex>) {
        val animalDrawing = Array(input.size) { Array(input.first().length) { Pipe.NO_PIPE }}
        animal.forEach {
            animalDrawing[it.coordinates.x][it.coordinates.y] = it.data
        }
        animalDrawing.forEach {
            println(it.joinToString(" "))
        }
    }

    fun parseAnimal(input: List<String>): List<Vertex> {
        val allVertices = input.mapIndexed { x, line ->
            line.mapIndexed { y, char ->
                Vertex(Coordinates(x, y), Pipe.fromChar(char))
            }.toTypedArray()
        }.toTypedArray()

        val adjacencyMap = buildMap {
            allVertices.forEach { line ->
                line.forEach { vertex ->
                    addNeighborsToAdjacencyMap(this, vertex, allVertices)
                }
            }
        }

        var startX = 0
        var startY = 0
        allVertices.forEachIndexed { x, line ->
            val y = line.indexOfFirst { it.data == Pipe.STARTING_POSITION }
            if (y != -1) {
                startX = x
                startY = y
                return@forEachIndexed
            }
        }
        val startVertex = Vertex(Coordinates(startX, startY), Pipe.STARTING_POSITION)
        val neighborsOfStart = adjacencyMap.mapNotNull {
            if (it.value.contains(startVertex)) {
                it.key
            } else {
                null
            }
        }
        val first = neighborsOfStart.first()
        val last = neighborsOfStart.last()

        var previousLocation: Vertex = startVertex
        var currentLocation: Vertex = first
        val animal = mutableListOf(startVertex)
        while (currentLocation != last) {
            animal.add(currentLocation)
            val firstNeighbor = adjacencyMap[currentLocation]!!.first()
            val secondNeighbor = adjacencyMap[currentLocation]!!.last()
            if (firstNeighbor != previousLocation && firstNeighbor != startVertex) {
                previousLocation = currentLocation
                currentLocation = firstNeighbor
            } else if (secondNeighbor != previousLocation && secondNeighbor != startVertex) {
                previousLocation = currentLocation
                currentLocation = secondNeighbor
            }
        }
        animal.add(currentLocation)
        animal.add(startVertex)
        return animal
    }

    /**
     * Algorithm: Shoelace
     * https://en.wikipedia.org/wiki/Shoelace_formula
     */
    fun calculateArea(data: List<Vertex>): Int {
        return (0..data.size-2).sumOf { i->
            data[i].coordinates.x * data[i+1].coordinates.y -
                    data[i+1].coordinates.x * data[i].coordinates.y
        }.div(2).absoluteValue
    }

    /**
     * Algorithm: Pick's theorem
     * https://en.wikipedia.org/wiki/Pick%27s_theorem
     */
    fun calculateAreaWithoutInteriorPoints(data: List<Vertex>): Int {
        return data.size / 2 - 1
    }

    fun part1(input: List<String>): Int {
        val animal = parseAnimal(input)
        return animal.size / 2
    }

    fun part2(input: List<String>): Int {
        val animal = parseAnimal(input)
        printAnimal(input, animal)
        return calculateArea(animal) - calculateAreaWithoutInteriorPoints(animal)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 8)
    val testInput2 = readInput("Day10_test2")
    check(part2(testInput2) == 10)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}
