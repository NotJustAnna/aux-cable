package net.notjustanna.ui.utils

import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import kotlin.system.exitProcess

object CloseWindowListener : WindowAdapter() {
    override fun windowClosing(e: WindowEvent) {
        exitProcess(0)
    }
}