package net.notjustanna.auxcable.api

import com.linecorp.armeria.server.annotation.Post
import java.awt.Desktop
import java.net.URI

class DesktopService {
    @Post("/discord-developer-portal")
    fun discordDeveloperPortal() {
        Desktop.getDesktop().browse(URI("https://discord.com/developers/applications"))
    }

    @Post("/jack")
    fun jack() {
        Desktop.getDesktop().browse(URI("https://jackaudio.org/"))
    }

    @Post("/cadence")
    fun cadence() {
        Desktop.getDesktop().browse(URI("https://kx.studio/Applications"))
    }

    @Post("/blackhole")
    fun blackhole() {
        Desktop.getDesktop().browse(URI("https://existential.audio/blackhole/"))
    }

    @Post("/ndi-tools")
    fun ndiTools() {
        Desktop.getDesktop().browse(URI("https://ndi.video/tools/virtual-input/"))
    }

    @Post("/vb-cable")
    fun vbCable() {
        Desktop.getDesktop().browse(URI("https://vb-audio.com/Cable/"))
    }

    @Post("/voicemeeter")
    fun voicemeeter() {
        Desktop.getDesktop().browse(URI("https://vb-audio.com/Voicemeeter/"))
    }
}