import java.awt.Color
import java.awt.Graphics
import java.awt.Image
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.WindowConstants.EXIT_ON_CLOSE
import kotlin.system.exitProcess

object SwingCanvas : JPanel(), AbstractCanvas {
    override val windowSize = Dimension(500, 500)
    override val fieldDimension = Dimension(20, 20)
    override fun getPreferredSize() = java.awt.Dimension(500, 500)

    private val frame = JFrame()
    private lateinit var game: Game

    override fun onStart(game: Game) {
        this.game = game
    }

    override fun onRestart(game: Game) {
        onStart(game)
    }

    override fun onGameEnd() {
        when(JOptionPane.showOptionDialog(
            frame,
            "Game Over - Score: ${game.snakeFields.size}",
            "Game Over",
            JOptionPane.YES_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            arrayOf("Restart"),
            null)) {
            0 -> restartGame()
            else -> exitProcess(0)
        }
    }

    override fun update() {

    }

    init {
        frame.defaultCloseOperation = EXIT_ON_CLOSE
        frame.isResizable = false

        frame.add(this)
        frame.addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent) {
                game.keyPressed(e.keyChar)
            }
            override fun keyPressed(e: KeyEvent) {}
            override fun keyReleased(e: KeyEvent) {}
        })

        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true

        background = Color.DARK_GRAY
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val width = windowSize.width / fieldDimension.width
        val height = windowSize.height / fieldDimension.height
        for (x in 0 until fieldDimension.width) {
            for (y in 0 until fieldDimension.height) {
                g.color = when {
                    game.snakeFields.last() == Vector(x, y) -> Color.GREEN
                    game.snakeFields.contains(Vector(x, y)) -> Color.BLUE
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

        repaint()
    }
}