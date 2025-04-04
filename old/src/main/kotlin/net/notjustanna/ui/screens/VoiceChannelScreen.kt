package net.notjustanna.ui.screens

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.notjustanna.audio.discord.AuxSendHandler
import net.notjustanna.audio.system.AudioInput
import net.notjustanna.audio.system.AudioInputs
import net.notjustanna.ui.utils.CloseWindowListener
import net.notjustanna.ui.utils.SpringUtilities
import net.notjustanna.ui.utils.consequenceless
import java.awt.Dialog
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.*
import kotlin.math.min

class VoiceChannelScreen(
    val jda: JDA,
    val channel: VoiceChannel
) {
    val dialog = JDialog(null as? Dialog, "Discord's Aux Cable", true)

    val inputsComboBox = JComboBox<InputSelect>()
    val leaveBtn = JButton("Leave")
    val muted = JCheckBox("Muted", true)

    val backgroundTask = Thread.ofVirtual().unstarted {
        try {
            while (true) {
                rebuildInputs()
                Thread.sleep(500)
            }
        } catch (e: InterruptedException) {
            // Do nothing
        }
    }

    fun changeInputs(input: AudioInput?) {
        val prev = channel.guild.audioManager.sendingHandler as? AuxSendHandler
        if (input != null) {
            val aux = AuxSendHandler.open(input)
            if (aux != null) {
                channel.guild.audioManager.sendingHandler = aux
            }
        } else {
            channel.guild.audioManager.sendingHandler = null
        }
        if (prev != null) {
            prev.close()
        }
    }

    init {
        inputsComboBox.addItem(InputSelect.Empty)
        muted.isEnabled = false
    }

    fun init() {
        leaveBtn.addActionListener {
            channel.guild.audioManager.closeAudioConnection()
            exit()
        }

        inputsComboBox.addActionListener {
            val selected = inputsComboBox.selectedItem as? InputSelect.Item

            if (muted.isEnabled != (selected != null)) {
                muted.consequenceless {
                    it.isEnabled = selected != null
                }
            }

            if (!muted.isSelected) {
                changeInputs(selected?.input)
            }
        }

        muted.addActionListener {
            if (muted.isSelected) {
                changeInputs(null)
            } else {
                changeInputs((inputsComboBox.selectedItem as? InputSelect.Item)?.input)
            }
        }

        dialog.addWindowListener(CloseWindowListener)
        backgroundTask.start()

        dialog.add(mainContent())
        dialog.pack()
        dialog.size = dialog.size.let { Dimension(400, it.height) }
        dialog.isResizable = false
        dialog.setLocationRelativeTo(null)
        SwingUtilities.invokeLater {
            dialog.isVisible = true
        }
    }

    fun mainContent(): JPanel {
        val root = JPanel()
        root.layout = BoxLayout(root, BoxLayout.Y_AXIS)
        root.border = BorderFactory.createEmptyBorder(8, 8, 8, 8)

        val grid = JPanel()
        grid.layout = SpringLayout()

        grid.add(JLabel("Input"))
        grid.add(inputsComboBox)
        grid.add(Box.createVerticalGlue())
        grid.add(muted)

        SpringUtilities.makeCompactGrid(grid, 2, 2, 8, 0, 8, 8)

        root.add(grid)

        val buttons = JPanel()
        buttons.layout = FlowLayout(FlowLayout.CENTER, 4, 0)

        buttons.add(leaveBtn)

        root.add(buttons)

        return root
    }

    private fun rebuildInputs() {
        val inputs = AudioInputs.all

        val inputOptions = mutableListOf<InputSelect>()
        inputOptions.add(if (inputs.isEmpty()) InputSelect.Empty else InputSelect.NotSelected)
        inputOptions.addAll(inputs.map { InputSelect.Item(it) })

        SwingUtilities.invokeLater {
            val selected = (inputsComboBox.selectedItem as? InputSelect.Item)?.input?.let { it.name to it.device }

            val inputList = mutableListOf<InputSelect>()
            for (i in 0 until inputsComboBox.itemCount) {
                inputList.add(inputsComboBox.getItemAt(i))
            }

            var foundSelected = false

            inputsComboBox.consequenceless {
                if (inputList != inputOptions) {
                    // first order of business is compare and set the first item
                    if (inputList.isNotEmpty() && inputOptions.isNotEmpty()) {
                        val oldFirst = inputList.firstOrNull()
                        val newFirst = inputOptions.firstOrNull()
                        if (oldFirst != newFirst) {
                            inputsComboBox.removeItem(oldFirst)
                            inputsComboBox.insertItemAt(newFirst, 0)
                        }
                        if (selected == null) {
                            inputsComboBox.selectedItem = newFirst
                            foundSelected = true
                        }
                        inputList.remove(oldFirst)
                        inputOptions.remove(newFirst)
                    }

                    for (i in 0 until min(inputList.size, inputOptions.size)) {
                        if (inputList[i] != inputOptions[i]) {
                            inputsComboBox.removeItem(inputList[i])
                            inputsComboBox.insertItemAt(inputOptions[i], i)
                            if (selected != null && selected == (inputOptions[i] as? InputSelect.Item)?.input?.let { it.name to it.device }) {
                                inputsComboBox.selectedItem = inputOptions[i]
                                foundSelected = true
                            }
                        }
                    }

                    for (i in min(inputList.size, inputOptions.size) until inputList.size) {
                        inputsComboBox.removeItem(inputList[i])
                        if (selected != null && selected == (inputOptions[i] as? InputSelect.Item)?.input?.let { it.name to it.device }) {
                            // oops, your device is gone
                            inputsComboBox.selectedItem = inputOptions[0]
                            foundSelected = false
                        }
                    }

                    for (i in min(inputList.size, inputOptions.size) until inputOptions.size) {
                        inputsComboBox.addItem(inputOptions[i])
                        if (selected != null && selected == (inputOptions[i] as? InputSelect.Item)?.input?.let { it.name to it.device }) {
                            inputsComboBox.selectedItem = inputOptions[i]
                            foundSelected = true
                        }
                    }

                    if (!foundSelected) {
                        inputsComboBox.selectedItem = inputOptions[0]
                    }
                    inputsComboBox.isEnabled = inputOptions.size > 1
                    muted.consequenceless {
                        val shouldBeEnabled = inputOptions.size > 1
                                && (inputsComboBox.selectedItem as? InputSelect.Item)?.input != null
                        if (it.isEnabled != shouldBeEnabled) {
                            it.isEnabled = shouldBeEnabled
                        }
                        val shouldBeSelected = inputOptions.size == 1
                                || (inputsComboBox.selectedItem as? InputSelect.Item)?.input == null
                        if (it.isSelected != shouldBeSelected) {
                            it.isSelected = shouldBeSelected
                        }
                    }
                }
            }
        }
    }

    fun exit() {
        channel.guild.audioManager.sendingHandler = null
        dialog.dispose()
        GuildSelectScreen(jda).init()
    }

    sealed class InputSelect {
        class Item(val input: AudioInput) : InputSelect() {
            override fun toString(): String {
                return "${input.name} (${input.device})"
            }

            override fun hashCode(): Int {
                return toString().hashCode()
            }

            override fun equals(other: Any?): Boolean {
                return other is Item && other.toString() == toString()
            }
        }

        object NotSelected : InputSelect() {
            override fun toString(): String {
                return "Select an input"
            }
        }

        object Empty : InputSelect() {
            override fun toString(): String {
                return "No inputs"
            }
        }
    }
}