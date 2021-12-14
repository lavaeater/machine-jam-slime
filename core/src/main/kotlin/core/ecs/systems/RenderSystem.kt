package core.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import core.ecs.components.SpriteComponent
import ktx.ashley.allOf
import ktx.graphics.use
import space.earlygrey.shapedrawer.ShapeDrawer

class RenderSystem(
    private val batch: PolygonSpriteBatch,
    private val shapeDrawer: ShapeDrawer,
    private val world: World,
    private val camera: OrthographicCamera) : IteratingSystem(allOf( SpriteComponent::class).get()) {

    private val box2DDebugRenderer = Box2DDebugRenderer()
    private val map = Rectangle(-100f, -100f, 200f,200f)

    override fun update(deltaTime: Float) {
        batch.use {
            renderMap(deltaTime)
            super.update(deltaTime)
        }
        box2DDebugRenderer.render(world, camera.combined)
    }

    private fun renderMap(deltaTime: Float) {
        /*
        I just want a square
         */
        shapeDrawer.rectangle(map)
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {

    }
}