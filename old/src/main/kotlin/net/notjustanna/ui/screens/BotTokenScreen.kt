package net.notjustanna.ui.screens

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.exceptions.InvalidTokenException
import net.dv8tion.jda.api.hooks.EventListener
import net.notjustanna.ui.utils.CloseWindowListener
import net.notjustanna.ui.utils.SpringUtilities
import net.notjustanna.ui.utils.WaitingDialog
import java.awt.Dialog
import java.awt.FlowLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption
import javax.swing.*
import kotlin.system.exitProcess

class BotTokenScreen {
    val dialog = JDialog(null as? Dialog, "Discord's Aux Cable", true)

    val tokenField = JTextField()
    val remindMe = JCheckBox("Save token")
    val loginBtn = JButton("Login")
    val exitBtn = JButton("Exit")

    fun init() {
        exitBtn.addActionListener { exitProcess(0) }
        loginBtn.addActionListener { tryLogin() }

        dialog.addWindowListener(CloseWindowListener)

        dialog.add(mainContent())
        dialog.pack()
        if (remindMe.isSelected) {
            loginBtn.requestFocusInWindow()
        }
        dialog.isResizable = false
        dialog.setLocationRelativeTo(null)
        SwingUtilities.invokeLater {
            dialog.isVisible = true
        }
    }

    fun mainContent(): JPanel {
        val token = File(".auxcable_token").takeIf { it.exists() }?.readText()
        if (token != null) {
            tokenField.text = token
            remindMe.isSelected = true
            loginBtn.requestFocusInWindow()
        }

        val root = JPanel()
        root.layout = BoxLayout(root, BoxLayout.Y_AXIS)
        root.border = BorderFactory.createEmptyBorder(8, 8, 8, 8)

        val grid = JPanel()
        grid.layout = SpringLayout()

        tokenField.columns = 40

        grid.add(JLabel("Token"))
        grid.add(tokenField)
        grid.add(Box.createVerticalGlue())
        grid.add(remindMe)

        SpringUtilities.makeCompactGrid(grid, 2, 2, 0, 0, 8, 8)

        root.add(grid)

        val buttons = JPanel()
        buttons.layout = FlowLayout(FlowLayout.CENTER, 4, 0)
        buttons.add(loginBtn)
        buttons.add(exitBtn)

        root.add(buttons)

        return root
    }

    fun tryLogin() {
        if (tokenField.text.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Token cannot be empty!\n\nTip: You need to have a Discord Bot to proceed.\nVisit https://discord.com/developers/applications to create one.", "Error", JOptionPane.ERROR_MESSAGE)
            return
        }

        val token = tokenField.text

        if (remindMe.isSelected) {
            asyncSaveToken(token)
        } else {
            asyncDeleteToken()
        }

        val login = WaitingDialog("Logging in...", "Logging in to Discord...", dialog)
        login.init()

        val listener = EventListener {
            if (it is ReadyEvent) {
                it.jda.removeEventListener(it.jda.registeredListeners.first())
                SwingUtilities.invokeLater {
                    login.isVisible = false
                    dialog.isVisible = false
                    GuildSelectScreen(it.jda).init()
                }
            }
        }

        Thread.startVirtualThread {
            try {
                JDABuilder.createLight(token)
                    .addEventListeners(listener)
                    .build()
            } catch (e: InvalidTokenException) {
                SwingUtilities.invokeLater {
                    login.isVisible = false
                    JOptionPane.showMessageDialog(dialog, "The provided token is invalid.\n\nTip: You need to have a Discord Bot to proceed.\nVisit https://discord.com/developers/applications to create one.", "Error", JOptionPane.ERROR_MESSAGE)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                SwingUtilities.invokeLater {
                    login.isVisible = false
                    JOptionPane.showMessageDialog(dialog, listOfNotNull(e.javaClass.simpleName, e.localizedMessage).joinToString(": ") + "\n\nThe full error was logged on the console.", "Error", JOptionPane.ERROR_MESSAGE)
                }
            }
        }
    }

    private fun asyncSaveToken(token: String) {
        Thread.startVirtualThread {
            val file = File(".auxcable_token")
            if (!file.exists()) {
                file.createNewFile()
                Files.setAttribute(file.toPath(), "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
            }
            file.writeText(token)
        }
    }

    private fun asyncDeleteToken() {
        Thread.startVirtualThread {
            val file = File(".auxcable_token")
            file.delete()
        }
    }
}