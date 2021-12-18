package core

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.ExtendViewport
import core.Factories.createSlime
import core.Factories.obstacle
import core.Factories.platform
import injection.Context.inject
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.box2d.body
import ktx.box2d.edge
import ktx.math.random
import ktx.math.vec2
import ktx.math.vec3
import java.lang.Math.pow
import kotlin.math.pow

val world: World
    get() {
        return inject()
    }

val engine: Engine
    get() {
        return inject()
    }

sealed class Direction {
    object Up : Direction()
    object Down : Direction()
    object Left : Direction()
    object Right : Direction()
    object Center : Direction()
}

object ControlObject {
    var up = false
    var down = false
    var left = false
    var right = false
    var rightTrigger = false
    var rightTriggerEnabled = false
    val leftTriggerOnCallbacks = mutableListOf<() -> Unit>()
    val leftTriggerOffCallbacks = mutableListOf<() -> Unit>()
    val rightTriggerOnCallbacks = mutableListOf<() -> Unit>()
    val rightTriggerOffCallbacks = mutableListOf<() -> Unit>()

    val horizontalDirection get() = if (left) Direction.Left else if (right) Direction.Right else Direction.Center
    val verticalDirection get() = if (up) Direction.Up else if (down) Direction.Down else Direction.Center

    fun leftTriggerStart() {
        for (method in leftTriggerOnCallbacks)
            method()
    }

    fun leftTriggerStop() {
        for (method in leftTriggerOffCallbacks)
            method()
    }

    fun rightTriggerStart() {
        for (method in rightTriggerOnCallbacks)
            method()
    }

    fun rightTriggerStop() {
        for (method in rightTriggerOffCallbacks)
            method()
    }

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
    private val camera: OrthographicCamera
) : KtxScreen, KtxInputAdapter {
    private val image = Texture("logo.png".toInternalFile(), true).apply {
        setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Linear
        )
    }
    private val batch = SpriteBatch()

    override fun show() {
        Gdx.input.inputProcessor = this
        //ball(vec2(5f,5f))
        //blobEntity(vec2(0f, 0f), 10f)
        createSlime(vec2(0f, 0f), 10f, 40, .5f)
        createLevel(1)
    }

    /**
     * The point of the game is to crawl to the top of a pipe. There might be things
     * falling down the pipe
     * and perhaps machines in here somewhere
     *
     * The pipe is represented by a bunch of vertices.
     * They never turn more than 90 degrees.
     * They never turn same direction twice.
     * The move forward some number of meters per iteration
     */
    fun createLevel(level: Int) {

        var lengthLeft = 10f.pow(level)

        /*
        1. While going straight, the chance of a turn grows larger with every
        meter travelled
         */
        val vertices = mutableListOf(vec2()) // we start at 0,0
        var chanceOfTurn = 0f
        val randomRange = 0f..99f
        var currentDirection: MapDirection = MapDirection.Up
        var lastVertex = vertices.first()
        while (lengthLeft > 0f) {
            if (randomRange.random() < chanceOfTurn) {
                chanceOfTurn = 5f
                currentDirection = MapDirection.possibleTurns[currentDirection]!!.random()
            } else {
                chanceOfTurn += 5f
            }
            lastVertex = lastVertex.cpy().add(currentDirection.directionVector.scl(5f))
            lengthLeft -= 5f
            vertices.add(lastVertex)
        }

        /*
        2. Create two edges, left and right, by simply
        moving the left edge 2.5f up and left, basically vec2(-2.5f, 2.5f)
         */
        for ((index, vertex) in vertices.withIndex()) {
            if (index == 0) {
                world.body {
                    type = BodyDef.BodyType.StaticBody
                    val nextVertex = vertices[index + 1]
                    edge(
                        vec2(-2.5f, 0f),
                        vec2(nextVertex.x - 2.5f, nextVertex.y + 2.5f)
                    ) {
                    }
                    edge(
                        vec2(2.5f, 0f),
                        vec2(nextVertex.x + 2.5f, nextVertex.y - 2.5f)
                    ) {
                    }
                }
            } else if (index < vertices.lastIndex) {
                world.body {
                    type = BodyDef.BodyType.StaticBody
                    val nextVertex = vertices[index + 1]
                    edge(
                        vec2(vertex.x - 2.5f, vertex.y + 2.5f),
                        vec2(nextVertex.x - 2.5f, nextVertex.y + 2.5f)
                    ) {
                    }
                    edge(
                        vec2(vertex.x + 2.5f, vertex.y - 2.5f),
                        vec2(nextVertex.x + 2.5f, nextVertex.y - 2.5f)
                    ) {
                    }
                }
            }
        }
    }

    override fun render(delta: Float) {
        handleInput()
        engine.update(delta)
    }

    private fun handleInput() {
        ControlObject.directionVector.set(x, y).nor()

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
        return when (keycode) {
            Input.Keys.W -> {
                y = 1f
                ControlObject.up = true
                ControlObject.down = false
                true
            }
            Input.Keys.A -> {
                ControlObject.left = true
                ControlObject.right = false
                x = 1f
                true
            }
            Input.Keys.S -> {
                ControlObject.up = false
                ControlObject.down = true
                y = -1f
                true
            }
            Input.Keys.D -> {
                ControlObject.left = false
                ControlObject.right = true
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
        if (button == Input.Buttons.LEFT) {
            ControlObject.leftTrigger = true
            ControlObject.leftTriggerStart()
            return true
        }
        if (button == Input.Buttons.RIGHT) {
            ControlObject.rightTrigger = true
            ControlObject.rightTriggerStart()
            return true
        }
        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Input.Buttons.LEFT) {
            ControlObject.leftTrigger = false
            ControlObject.leftTriggerEnabled = true
            ControlObject.leftTriggerStop()
            return true
        }
        if (button == Input.Buttons.RIGHT) {
            ControlObject.rightTrigger = false
            ControlObject.rightTriggerEnabled = true
            ControlObject.rightTriggerStop()
            return true
        }
        return super.touchUp(screenX, screenY, pointer, button)
    }

    override fun keyUp(keycode: Int): Boolean {
        return when (keycode) {
            Input.Keys.W -> {
                y = 0f
                ControlObject.up = false
                true
            }
            Input.Keys.A -> {
                ControlObject.left = false
                x = 0f
                true
            }
            Input.Keys.S -> {
                ControlObject.down = false
                y = 0f
                true
            }
            Input.Keys.D -> {
                ControlObject.right = false
                x = 0f
                true
            }
            else -> super.keyDown(keycode)
        }
    }
}

sealed class MapDirection(val directionVector: Vector2) {
    object Up : MapDirection(vec2(0f, 1f))
    object Down : MapDirection(vec2(0f, -1f))
    object Left : MapDirection(vec2(-1f, 0f))
    object Right : MapDirection(vec2(1f, 0f))
    companion object {
        val allDirections = listOf(Up, Down, Left, Right)
        val opposites = mapOf(Up to Down, Down to Up, Left to Right, Right to Left)
        val possibleTurns = mapOf(
            Up to listOf(Left, Right),
            Down to listOf(Left, Right),
            Left to listOf(Down, Up),
            Right to listOf(Down, Up)
        )
    }
}