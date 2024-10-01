package net.notjustanna.ui.utils

import javax.swing.JCheckBox
import javax.swing.JComboBox

fun <E, T : JComboBox<E>> T.consequenceless(block: (T) -> Unit) {
    val listeners = actionListeners
    actionListeners.forEach { removeActionListener(it) }
    block(this)
    listeners.forEach { addActionListener(it) }
}

fun JCheckBox.consequenceless(block: (JCheckBox) -> Unit) {
    val listeners = actionListeners
    actionListeners.forEach { removeActionListener(it) }
    block(this)
    listeners.forEach { addActionListener(it) }
}
