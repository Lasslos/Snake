import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.MouseAdapter
import javax.swing.BorderFactory
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants.EXIT_ON_CLOSE


fun main() {
    val frame = JFrame()
    frame.defaultCloseOperation = EXIT_ON_CLOSE
    frame.size = Dimension(500, 500)
    frame.add(MyPanel)
    frame.setLocationRelativeTo(null)
    frame.isVisible = true
}

object Game {
    var appleField = Array(15) { Array(15) { false } }
    var snakeField = Array(15) { Array(15) { false } }
}

object MyPanel : JPanel() {


    override fun getPreferredSize(): Dimension {
        return Dimension(500, 500)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

    }
}