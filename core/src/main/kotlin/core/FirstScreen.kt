package core

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.JointDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.ExtendViewport
import core.ecs.components.BodyComponent
import core.ecs.components.CameraFollowComponent
import core.ecs.components.SpriteComponent
import injection.Context.inject
import ktx.app.KtxScreen
import ktx.ashley.entity
import ktx.ashley.with
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.circle
import ktx.math.vec2
import kotlin.math.sqrt

val world: World
    get() {
        return inject()
    }

val engine: Engine
    get() {
        return inject()
    }

fun platform(at: Vector2, width: Float, height: Float) {
    val body = world.body {
        type = BodyDef.BodyType.StaticBody
        position.set(at)
        box(width, height) {}
    }
    engine.entity {
        with<BodyComponent> {
            this.body = body
        }
        with<CameraFollowComponent>()
        with<SpriteComponent>()
    }
}

fun ball(at: Vector2) {
    val body = world.body {
        type = BodyDef.BodyType.DynamicBody
        position.set(at)
        circle(1f) {
        }
    }
    engine.entity {
        with<BodyComponent> {
            this.body = body
        }
        with<CameraFollowComponent>()
        with<SpriteComponent>()
    }
}

fun blobEntity(at: Vector2, length: Float) {
    val l = length
    val a = vec2(l,0f)
    val b = vec2(l / 2f, sqrt(3f)*l/2f)
    val c = vec2(-l/2, sqrt(3f) * l/2f)
    val d = vec2(-l,0f)
    val e = vec2(-l/2, sqrt(3f) * -l/2f)
    val f = vec2(l/2, sqrt(3f) * -l/2f)
    val vertices = listOf(a,b,c,d,e,f)

    for(vertex in vertices) {
        val body = world.body {
            type = BodyDef.BodyType.DynamicBody
            position.set(at.x + vertex.x, at.y + vertex.y)
            circle(2f, vec2(0f, 0f)) {}
        }
    }
}

class FirstScreen(
    private val engine: Engine,
    private val viewPort: ExtendViewport,
    private val camera: OrthographicCamera) : KtxScreen {
    private val image = Texture("logo.png".toInternalFile(), true).apply { setFilter(
        Texture.TextureFilter.Linear,
        Texture.TextureFilter.Linear
    ) }
    private val batch = SpriteBatch()

    override fun show() {
        ball(vec2(5f,5f))
        blobEntity(vec2(0f, 0f), 10f)
        platform(vec2(-10f,-20f), 40f, 2.5f)
    }

    override fun render(delta: Float) {
        engine.update(delta)
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
}