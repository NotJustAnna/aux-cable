
import net.notjustanna.ui.screens.BotTokenScreen
import javax.swing.UIManager

fun main() {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (e: Exception) {
        e.printStackTrace()
    }

    BotTokenScreen().init()
}
