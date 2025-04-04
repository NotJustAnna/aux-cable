package net.notjustanna.ui.screens

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.notjustanna.ui.utils.CloseWindowListener
import net.notjustanna.ui.utils.SpringUtilities
import net.notjustanna.ui.utils.WaitingDialog
import java.awt.Dialog
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*
import kotlin.system.exitProcess

class GuildSelectScreen(val jda: JDA) {
    val dialog = JDialog(null as? Dialog, "Discord's Aux Cable", true)

    val guildsComboBox = JComboBox<GuildSelect>()
    val voiceChannelsComboBox = JComboBox<VoiceChannelSelect>()
    val connectBtn = JButton("Connect")
    val logoutBtn = JButton("Logout")

    val backgroundTask = Thread.ofVirtual().unstarted {
        try {
            while (true) {
                rebuildGuilds()
                Thread.sleep(500)
            }
        } catch (e: InterruptedException) {
            // Do nothing
        }
    }

    init {
        guildsComboBox.addItem(GuildSelect.Empty)
        guildsComboBox.isEnabled = false
        voiceChannelsComboBox.addItem(VoiceChannelSelect.NotSelected)
        voiceChannelsComboBox.isEnabled = false
        connectBtn.isEnabled = false
    }

    fun init() {
        logoutBtn.addActionListener {
            backgroundTask.interrupt()
            jda.shutdown()
            dialog.dispose()
            BotTokenScreen().init()
        }

        connectBtn.addActionListener {
            tryConnect()
        }

        dialog.addWindowListener(CloseWindowListener)

        backgroundTask.start()

        guildsComboBox.addActionListener {
            rebuildChannels(guildsComboBox.selectedItem as? GuildSelect)
        }

        voiceChannelsComboBox.addActionListener {
            val canConnect = voiceChannelsComboBox.selectedItem is VoiceChannelSelect.Item
            if (connectBtn.isEnabled != canConnect) {
                connectBtn.isEnabled = canConnect
            }
        }

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

        grid.add(JLabel("Server"))
        grid.add(guildsComboBox)
        grid.add(JLabel("Voice Channel"))
        grid.add(voiceChannelsComboBox)
        grid.add(Box.createVerticalGlue())

        SpringUtilities.makeCompactGrid(grid, 2, 2, 8, 0, 8, 8)

        root.add(grid)

        val buttons = JPanel()
        buttons.layout = FlowLayout(FlowLayout.CENTER, 4, 0)

        buttons.add(connectBtn)
        buttons.add(logoutBtn)

        root.add(buttons)

        return root
    }

    fun rebuildGuilds() {
        val guilds = jda.guilds
        val selectedGuild = guildsComboBox.selectedItem

        val guildOptions = mutableListOf<GuildSelect>()
        guildOptions.add(
            if (guilds.isEmpty()) {
                GuildSelect.Empty
            } else {
                GuildSelect.NotSelected
            }
        )
        guildOptions.addAll(guilds.map { GuildSelect.Item(it) })

        SwingUtilities.invokeLater {
            val list = buildList {
                for (i in 0 until guildsComboBox.itemCount) {
                    add(guildsComboBox.getItemAt(i))
                }
            }
            if (list != guildOptions) {
                guildsComboBox.removeAllItems()
                guildOptions.forEach { guildsComboBox.addItem(it) }
                guildsComboBox.isEnabled = guildOptions.size > 1

                if (selectedGuild != null) {
                    guildsComboBox.selectedItem = when (selectedGuild) {
                        is GuildSelect.Item -> guildOptions.find { it is GuildSelect.Item && it.guild.id == selectedGuild.guild.id }
                        else -> selectedGuild
                    } ?: guildOptions.first()
                }

                if (guildOptions.size == 1 ||
                    guildsComboBox.selectedItem == GuildSelect.NotSelected ||
                    guildsComboBox.selectedItem == GuildSelect.Empty ||
                    (guildsComboBox.selectedItem as? GuildSelect.Item)?.guild != (voiceChannelsComboBox.selectedItem as? VoiceChannelSelect.Item)?.channel?.guild) {
                    rebuildChannels(guildsComboBox.selectedItem as? GuildSelect)
                }
            }
        }

    }

    private fun rebuildChannels(guildSelect: GuildSelect?) {
        val guild = when (guildSelect) {
            is GuildSelect.Item -> guildSelect.guild
            else -> null
        }

        val voiceChannels = guild?.voiceChannels ?: emptyList()
        val selectedChannel = voiceChannelsComboBox.selectedItem

        val voiceChannelOptions = mutableListOf<VoiceChannelSelect>()
        voiceChannelOptions.add(
            if (voiceChannels.isEmpty()) {
                VoiceChannelSelect.Empty
            } else {
                VoiceChannelSelect.NotSelected
            }
        )
        voiceChannelOptions.addAll(voiceChannels.map { VoiceChannelSelect.Item(it) })

        SwingUtilities.invokeLater {
            voiceChannelsComboBox.removeAllItems()
            voiceChannelOptions.forEach { voiceChannelsComboBox.addItem(it) }
            voiceChannelsComboBox.isEnabled = voiceChannelOptions.size > 1

            if (selectedChannel != null) {
                voiceChannelsComboBox.selectedItem = when (selectedChannel) {
                    is VoiceChannelSelect.Item -> voiceChannelOptions.find { it is VoiceChannelSelect.Item && it.channel.id == selectedChannel.channel.id }
                    else -> selectedChannel
                } ?: voiceChannelOptions.first()
            }

            val canConnect = voiceChannelsComboBox.selectedItem is VoiceChannelSelect.Item
            if (connectBtn.isEnabled != canConnect) {
                connectBtn.isEnabled = canConnect
            }
        }
    }

    fun tryConnect() {
        val channel = (voiceChannelsComboBox.selectedItem as? VoiceChannelSelect.Item)?.channel ?: return

        val login = WaitingDialog("Connecting...", "Connecting to \"${channel.guild.name} > ${channel.name}\"...", dialog)
        login.init()

        jda.listenOnce(GuildVoiceUpdateEvent::class.java)
            .filter { it.channelJoined?.id == channel.id && it.member.user.id == jda.selfUser.id }
            .subscribe {
                backgroundTask.interrupt()
                login.dispose()
                dialog.dispose()

                VoiceChannelScreen(jda, channel).init()
            }


        channel.guild.audioManager.openAudioConnection(channel)
    }

    sealed class GuildSelect {
        data class Item(val guild: Guild) : GuildSelect() {
            override fun toString(): String {
                return guild.name
            }
        }
        object NotSelected : GuildSelect() {
            override fun toString(): String {
                return "Select a server"
            }
        }
        object Empty : GuildSelect() {
            override fun toString(): String {
                return "No servers"
            }
        }
    }

    sealed class VoiceChannelSelect {
        data class Item(val channel: VoiceChannel) : VoiceChannelSelect() {
            override fun toString(): String {
                return channel.name
            }
        }
        object NotSelected : VoiceChannelSelect() {
            override fun toString(): String {
                return "Select a channel"
            }
        }
        object Empty : VoiceChannelSelect() {
            override fun toString(): String {
                return "No voice channels"
            }
        }
    }
}
