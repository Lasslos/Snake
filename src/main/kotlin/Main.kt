import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Image
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.WindowConstants.EXIT_ON_CLOSE
import kotlin.random.Random
import kotlin.system.exitProcess

val windowSize = Dimension(500, 500)
val fieldDimension = Dimension(20, 20)

fun main() {
    MyFrame
}

object MyFrame : JPanel() {
    override fun getPreferredSize(): Dimension = windowSize

    val frame = JFrame()
    var game = Game(this)

    init {
        frame.defaultCloseOperation = EXIT_ON_CLOSE
        frame.size = windowSize
        frame.add(MyFrame)
        frame.setLocationRelativeTo(null)
        frame.isResizable = false
        frame.isVisible = true
        frame.addKeyListener(game)

        background = Color.DARK_GRAY
        game.run()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val width = windowSize.width / fieldDimension.width
        val height = windowSize.height / fieldDimension.height
        for (x in 0 until fieldDimension.width) {
            for (y in 0 until fieldDimension.height) {
                g.color = when {
                    game.snakeFields.last() == Point(x, y) -> Color.GREEN
                    game.snakeFields.contains(Point(x, y)) -> Color.BLUE
                    else -> Color.DARK_GRAY
                }
                g.fillRect(x * width, y * height, width, height)
            }
        }

        val myImage: Image = ImageIO.read(javaClass.getResource("apple.png"))
            .getScaledInstance(width, height, Image.SCALE_SMOOTH)
        g.drawImage(myImage, game.appleField.x * width, game.appleField.y * height, width, height, null)

        g.color = Color.BLACK
        for (x in 0..fieldDimension.width) {
            g.drawLine(x * width, 0, x * width, windowSize.height)
        }
        for (y in 0..fieldDimension.height) {
            g.drawLine(0, y * height, windowSize.width, y * height)
        }
    }
}

class Game(private val parentFrame: MyFrame): KeyListener {
    //First element is end of snake, Last element is head
    val snakeFields = MutableList(4) { Point(fieldDimension.width / 2, it) }
    var appleField = Point(18, 4)
    private var gameOver = false

    private var currentDirection = Point(0, +1)
    private var nextDirection = Point(0, +1)

    fun run() {
        CoroutineScope(Dispatchers.Default).launch {
            while (!gameOver) {
                delay(250)
                launch {
                    next()
                    MyFrame.repaint()
                }
            }
        }
    }

    fun next() {
        currentDirection = nextDirection
        val nextHeadPoint = snakeFields.last() + currentDirection
        if (nextHeadPoint.x < 0 || nextHeadPoint.y < 0 ||
            nextHeadPoint.x >= fieldDimension.width || nextHeadPoint.y >= fieldDimension.height ||
                snakeFields.contains(nextHeadPoint)) {
            gameOver = true

            when(JOptionPane.showOptionDialog(
                parentFrame,
                "Game Over",
                "Game Over",
                JOptionPane.YES_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                arrayOf("Restart"),
                null)) {
                0 -> {
                    parentFrame.game = Game(parentFrame)
                    parentFrame.frame.removeKeyListener(this)
                    parentFrame.frame.addKeyListener(parentFrame.game)
                    parentFrame.game.run()
                }
                else -> exitProcess(0)
            }

            return
        }

        snakeFields.add(nextHeadPoint)

        if (nextHeadPoint != appleField) {
            snakeFields.removeFirst()
        } else {
            appleField = Point(Random.nextInt(0, fieldDimension.width - 1),
                Random.nextInt(0, fieldDimension.height - 1))
        }
    }

    override fun keyTyped(e: KeyEvent?) {}
    override fun keyReleased(e: KeyEvent?) {}

    override fun keyPressed(e: KeyEvent) {
        val newDirection = when (e.keyChar) {
            'w' -> Point(0, -1)
            'a' -> Point(-1, 0)
            's' -> Point(0, 1)
            'd' -> Point(1, 0)
            else -> { return; }
        }
        //Check if Direction is Opposite to prevent an 180° turn
        if (currentDirection + newDirection != Point(0, 0)) {
            nextDirection = newDirection
        }
    }
}

class Point(val x: Int, val y: Int) {
    private fun copyWith(x: Int = this.x, y: Int = this.y) = Point(x, y)
    operator fun plus(other: Point) = this.copyWith(this.x + other.x, this.y + other.y)
    override fun equals(other: Any?): Boolean {
        if (other !is Point) return false
        return x == other.x && y == other.y
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}