package core

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.ExtendViewport
import core.Factories.ball
import core.Factories.blobEntity
import core.Factories.platform
import injection.Context.inject
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