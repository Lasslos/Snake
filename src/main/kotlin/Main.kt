import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

const val delay = 150L

fun main() {
    val game = Game()
    canvas.onStart(game)
    run(game)
}

interface AbstractCanvas {
    val windowSize: Dimension
    val fieldDimension: Dimension

    fun onStart(game: Game)
    fun onRestart(game: Game)
    fun onGameEnd()

    fun update()
}

val canvas: AbstractCanvas = SwingCanvas //TODO

private fun run(game: Game) {
    CoroutineScope(Dispatchers.Default).launch {
        while (!game.gameOver) {
            delay(delay)
            game.next()
        }
        canvas.onGameEnd()
    }
}
fun restartGame() {
    val game = Game()
    canvas.onRestart(game)
    run(game)
}

class Game {
    val snakeFields = MutableList(4) { Vector(canvas.fieldDimension.width / 2, it) }
    var appleField = Vector(18, 4)
    var gameOver = false

    private var currentDirection = Vector(0, +1)
    private var nextDirection = Vector(0, +1)

    fun keyPressed(char: Char) {
        val newDirection = when (char) {
            'w' -> Vector(0, -1)
            'a' -> Vector(-1, 0)
            's' -> Vector(0, 1)
            'd' -> Vector(1, 0)
            else -> { return; }
        }
        //Check if Direction is Opposite to prevent an 180° turn
        if (currentDirection + newDirection != Vector(0, 0)) {
            nextDirection = newDirection
        }
    }

    fun next() {
        currentDirection = nextDirection
        val nextHeadPoint = snakeFields.last() + currentDirection
        if (nextHeadPoint.x < 0 || nextHeadPoint.y < 0 ||
            nextHeadPoint.x >= canvas.fieldDimension.width || nextHeadPoint.y >= canvas.fieldDimension.height ||
            snakeFields.contains(nextHeadPoint)) {
            gameOver = true
            return
        }

        snakeFields.add(nextHeadPoint)

        if (nextHeadPoint != appleField) {
            snakeFields.removeFirst()
        } else {
            appleField = Vector(Random.nextInt(0, canvas.fieldDimension.width - 1),
                Random.nextInt(0, canvas.fieldDimension.height - 1))
        }
        canvas.update()
    }
}

class Vector(val x: Int, val y: Int) {
    operator fun plus(other: Vector) = Vector(this.x + other.x, this.y + other.y)
    override fun equals(other: Any?): Boolean {
        if (other !is Vector) return false
        return x == other.x && y == other.y
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}
class Dimension(val width: Int, val height: Int)