package core

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Joint
import com.badlogic.gdx.physics.box2d.JointDef
import com.badlogic.gdx.physics.box2d.World
import injection.Context.inject
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.box2d.body
import ktx.box2d.circle
import ktx.math.vec2
import kotlin.math.sqrt

fun world() : World {
    return inject()
}

fun blobEntity() {
    val position = vec2()
    val l = 1f
    val a = vec2(position.x + l,0f)
    val b = vec2(l / 2, (sqrt(3*l)/2))
    val c = vec2()
    val d = vec2()
    val e = vec2()
    val f = vec2()

    val constantVolumeJointDef = JointDef().apply {
        type = JointDef.JointType.DistanceJoint

    }
    for(i in 0 until 6) {

        val body = world().body {
            type = BodyDef.BodyType.DynamicBody
            circle(2f, vec2(0f, 0f)) {
                isSensor = true
            }
        }

    }
}

class FirstScreen(private val engine: Engine) : KtxScreen {
    private val image = Texture("logo.png".toInternalFile(), true).apply { setFilter(
        Texture.TextureFilter.Linear,
        Texture.TextureFilter.Linear
    ) }
    private val batch = SpriteBatch()

    override fun show() {

    }

    override fun render(delta: Float) {
        clearScreen(red = 0.7f, green = 0.7f, blue = 0.7f)
        engine.update(delta)
//        batch.use {
//            it.draw(image, 100f, 160f)
//        }
    }

    override fun dispose() {
        image.disposeSafely()
        batch.disposeSafely()
    }
}