@file:JvmName("DesktopLauncher")

package core.desktop

import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import core.MachineJamSlime

/** Launches the desktop (LWJGL) application. */
fun main() {
    LwjglApplication(MachineJamSlime(), LwjglApplicationConfiguration().apply {
        title = "machine-jam-slime"
        width = 640
        height = 480
        intArrayOf(128, 64, 32, 16).forEach{
            addIcon("libgdx$it.png", Files.FileType.Internal)
        }
    })
}
