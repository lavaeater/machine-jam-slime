package core

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.ExtendViewport
import core.Factories.createSlime
import core.Factories.platform
import injection.Context.inject
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.math.vec2
import ktx.math.vec3

val world: World
    get() {
        return inject()
    }

val engine: Engine
    get() {
        return inject()
    }

object ControlObject {
    var leftTriggerEnabled = true
    var leftTrigger = false
    val directionVector = vec2()
    val mousePosition3d = vec3()
    val mousePosition = vec2()
    val aimVector = vec2()
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
        Gdx.input.inputProcessor = this
        //ball(vec2(5f,5f))
        //blobEntity(vec2(0f, 0f), 10f)
        createSlime(vec2(0f, 0f), 5f, 20, .5f)
        platform(vec2(-20f,-40f), 200f, 1.25f)
        platform(vec2(20f, -30f), 5f, 5f)
        platform(vec2(-20f, 60f), 5f, 5f)
        platform(vec2(20f, 60f), 5f, 5f)
        platform(vec2(30f, 20f), 5f, 5f)
    }

    override fun render(delta: Float) {
        handleInput()
        engine.update(delta)
    }

    private fun handleInput() {
        ControlObject.directionVector.set(x,y).nor()

        ControlObject.mousePosition3d.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        camera.unproject(ControlObject.mousePosition3d)
        ControlObject.mousePosition.set(ControlObject.mousePosition3d.x, ControlObject.mousePosition3d.y)
        ControlObject.aimVector.set(ControlObject.mousePosition).sub(vec2(camera.position.x, camera.position.y)).nor()
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
                x = 1f
                true
            }
            Input.Keys.S -> {
                y = -1f
                true
            }
            Input.Keys.D -> {
                x = -1f
                true
            }
//            Input.Keys.NUMPAD_ADD -> {
//                val primary: Graphics.Monitor = Gdx.graphics.primaryMonitor
//                val modes: Array<Graphics.DisplayMode> = Gdx.graphics.getDisplayModes(primary)
//                Gdx.graphics.setFullscreenMode(modes[11])
//                true
//            }
            else -> super.keyDown(keycode)
        }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if(button == Input.Buttons.LEFT) {
            ControlObject.leftTrigger = true
            return true
        }
        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if(button == Input.Buttons.LEFT) {
            ControlObject.leftTrigger = false
            ControlObject.leftTriggerEnabled = true
            return true
        }
        return super.touchUp(screenX, screenY, pointer, button)
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