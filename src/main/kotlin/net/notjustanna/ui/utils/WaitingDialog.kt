package net.notjustanna.ui.utils

import java.awt.Component
import javax.swing.*

class WaitingDialog(title: String, description: String, private val parent: Component) : JDialog(JFrame(), title, true) {
    val root = JPanel()
    val label = JLabel(description)

    fun init() {
        root.border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
        root.add(label)
        this.add(root)

        this.pack()
        this.addWindowListener(CloseWindowListener)
        this.setLocationRelativeTo(parent)
        this.isResizable = false
        SwingUtilities.invokeLater {
            this.isVisible = true
        }
    }
}
