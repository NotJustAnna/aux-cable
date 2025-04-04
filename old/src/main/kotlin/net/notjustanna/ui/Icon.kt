package net.notjustanna.ui

import javax.imageio.ImageIO

object Icon {
    val image by lazy {
        Icon::class.java.getResourceAsStream("icon.png").use(ImageIO::read)
    }
}