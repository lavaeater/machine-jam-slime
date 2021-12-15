package core

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.ExtendViewport
import core.Factories.ball
import core.Factories.platform
import injection.Context.inject
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.math.vec2

val world: World
    get() {
        return inject()
    }

val engine: Engine
    get() {
        return inject()
    }

object ControlObject {
    val directionVector = vec2()
}


class FirstScreen(
    private val engine: Engine,
    private val viewPort: ExtendViewport,
    private val camera: OrthographicCamera) : KtxScreen, KtxInputAdapter {
    private val image = Texture("logo.png".toInternalFile(), true).apply { setFilter(
        Texture.TextureFilter.Linear,
        Texture.TextureFilter.Linear
    ) }
    private val batch = SpriteBatch()

    override fun show() {
        ball(vec2(5f,5f))
        //blobEntity(vec2(0f, 0f), 10f)
        buildSlimeCircle(vec2(0f, 0f))
        platform(vec2(-20f,-50f), 100f, 2.5f)
    }

    override fun render(delta: Float) {
        updateDirectionVector()
        engine.update(delta)
    }

    private fun updateDirectionVector() {
        ControlObject.directionVector.set(x,y).nor()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        viewPort.update(width, height)
        batch.projectionMatrix = camera.combined
    }

    override fun dispose() {
        image.disposeSafely()
        batch.disposeSafely()
    }

    /**
     * Deal with some inputs
     */
    var x = 0f
    var y = 0f
    override fun keyDown(keycode: Int): Boolean {
        return when(keycode) {
            Input.Keys.W -> {
                y = 1f
                true
            }
            Input.Keys.A -> {
                x = -1f
                true
            }
            Input.Keys.S -> {
                y = -1f
                true
            }
            Input.Keys.D -> {
                x = 1f
                true
            }
            else -> super.keyDown(keycode)
        }
    }

    override fun keyUp(keycode: Int): Boolean {
        return when(keycode) {
            Input.Keys.W -> {
                y = 0f
                true
            }
            Input.Keys.A -> {
                x = 0f
                true
            }
            Input.Keys.S -> {
                y = 0f
                true
            }
            Input.Keys.D -> {
                x = 0f
                true
            }
            else -> super.keyDown(keycode)
        }
    }
}