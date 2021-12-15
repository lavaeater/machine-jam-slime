package injection

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.viewport.ExtendViewport
import core.FirstScreen
import core.ecs.systems.*
import injection.GameConstants.GAMEHEIGHT
import injection.GameConstants.GAMEWIDTH
import ktx.box2d.createWorld
import ktx.inject.Context
import ktx.inject.register
import ktx.math.vec2
import space.earlygrey.shapedrawer.ShapeDrawer

object Context {
    val context = Context()

    init {
        buildContext()
    }

    inline fun <reified T> inject(): T {
        return context.inject()
    }

    private fun createShapeDrawer() : ShapeDrawer {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.drawPixel(0, 0)
        val texture = Texture(pixmap) //remember to dispose of later
        pixmap.dispose()

        val shapeDrawerRegion = TextureRegion(texture, 0, 0, 1, 1)

        return ShapeDrawer(inject<PolygonSpriteBatch>() as Batch, shapeDrawerRegion)
    }

    private fun buildContext() {
        context.register {
            bindSingleton(PolygonSpriteBatch())
            bindSingleton(createShapeDrawer())
            bindSingleton(OrthographicCamera())
            bindSingleton(
                ExtendViewport(
                    GAMEWIDTH,
                    GAMEHEIGHT,
                    inject<OrthographicCamera>() as Camera
                )
            )
            bindSingleton(createWorld(vec2(0f, 0f)))
            bindSingleton(getEngine())
            bindSingleton(FirstScreen(inject(), inject(), inject()))
        }
    }

    private fun getEngine(): Engine {
        return PooledEngine().apply {
            addSystem(PhysicsUpdateSystem(inject()))
            addSystem(CameraFollowSystem(inject()))
            addSystem(PhysicsEntityUpdateSystem())
            addSystem(SlimeSystem())
            addSystem(RenderSystem(inject(), inject(), inject(), inject()))
        }
    }
}