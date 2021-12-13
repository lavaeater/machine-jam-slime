@file:JvmName("Lwjgl3Launcher")

package core.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import core.MachineJamSlime

/** Launches the desktop (LWJGL3) application. */
fun main() {
    Lwjgl3Application(MachineJamSlime(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("machine-jam-slime")
        setWindowedMode(640, 480)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
