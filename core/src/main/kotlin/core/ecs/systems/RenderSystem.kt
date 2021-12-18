package core.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import core.Assets
import core.ControlObject
import core.ecs.AshleyMappers.slimerComponent
import core.ecs.AshleyMappers.spriteComponent
import core.ecs.components.SlimerComponent
import core.ecs.components.SpriteComponent
import ktx.app.clearScreen
import ktx.ashley.allOf
import ktx.collections.toGdxArray
import ktx.graphics.use
import ktx.math.vec2
import space.earlygrey.shapedrawer.ShapeDrawer

class RenderSystem(
    private val batch: PolygonSpriteBatch,
    private val shapeDrawer: ShapeDrawer,
    private val world: World,
    private val camera: OrthographicCamera) : IteratingSystem(allOf( SpriteComponent::class).get()) {

    private val box2DDebugRenderer = Box2DDebugRenderer()
    private val map = Rectangle(0f,0f,200f,30f)

    override fun update(deltaTime: Float) {
        batch.projectionMatrix = camera.combined
        clearScreen(red = 85f / 255f, green = 132f / 255f, blue = 172f / 255f)
        batch.use {
            renderMap(deltaTime)
//            shapeDrawer.line(camera.position.x, camera.position.y, ControlObject.mousePosition.x, ControlObject.mousePosition.y)
//            val endVec = ControlObject.aimVector.cpy().scl(15f)
//            shapeDrawer.line(camera.position.x, camera.position.y, camera.position.x + endVec.x, camera.position.y + endVec.y, Color.RED)
            val rayCastEnd = vec2(camera.position.x, camera.position.y).add(ControlObject.aimVector.cpy().scl(200f))
            shapeDrawer.line(camera.position.x, camera.position.y, rayCastEnd.x, rayCastEnd.y, Color.ORANGE, .1f)

            super.update(deltaTime)
        }
        box2DDebugRenderer.render(world, camera.combined)
    }

    private fun renderMap(deltaTime: Float) {
        /*
        I just want a square
         */
        //shapeDrawer.rectangle(map)
        //shapeDrawer.filledCircle(5f * pixelsPerMeter,5f * pixelsPerMeter,2.5f, Color.RED)
    }

    private fun renderSlimer(slimer: SlimerComponent) {
        for(section in slimer.allSections) {
            val vertices = mutableListOf<Array<Float>>()
            vertices.add(arrayOf(section.first.position.x, section.first.position.y, Color.WHITE.toFloatBits(), 0f,1f))
            vertices.add(arrayOf(section.second.position.x, section.second.position.y, Color.WHITE.toFloatBits(), 0f,0f))
            vertices.add(arrayOf(section.third.position.x, section.third.position.y, Color.WHITE.toFloatBits(), 1f,0f))
            vertices.add(arrayOf(section.first.position.x, section.first.position.y, Color.WHITE.toFloatBits(), 1f,1f))
            val actualVertices = vertices.toTypedArray().flatten().toFloatArray()

            batch.draw(Assets.slimeTexture, actualVertices, 0, actualVertices.size)
        }
        for(rope in slimer.ropeySlimey) {
            lateinit var lastNode: Body
            for((index, node) in rope.nodes.keys.withIndex()) {
                if(index > 0) {
                    val vertices = mutableListOf<Array<Float>>()
                    vertices.add(arrayOf(node.position.x + 0.5f, node.position.y + 0.5f, Color.WHITE.toFloatBits(), 0f,1f))
                    vertices.add(arrayOf(node.position.x - 0.5f, node.position.y - 0.5f, Color.WHITE.toFloatBits(), 0f,0f))
                    vertices.add(arrayOf(lastNode.position.x - 0.5f, lastNode.position.y - 0.5f, Color.WHITE.toFloatBits(), 1f,0f))
                    vertices.add(arrayOf(lastNode.position.x + 0.5f, lastNode.position.y + 0.5f, Color.WHITE.toFloatBits(), 1f,1f))
                    val actualVertices = vertices.toTypedArray().flatten().toFloatArray()

                    batch.draw(Assets.slimeTexture, actualVertices, 0, actualVertices.size)
                }
                lastNode = node
            }
            val vertices = mutableListOf<Array<Float>>()
            val section = rope.triangle
            vertices.add(arrayOf(section.first.position.x, section.first.position.y, Color.WHITE.toFloatBits(), 0f,1f))
            vertices.add(arrayOf(section.second.position.x, section.second.position.y, Color.WHITE.toFloatBits(), 0f,0f))
            vertices.add(arrayOf(section.third.position.x, section.third.position.y, Color.WHITE.toFloatBits(), 1f,0f))
            vertices.add(arrayOf(section.first.position.x, section.first.position.y, Color.WHITE.toFloatBits(), 1f,1f))
            val actualVertices = vertices.toTypedArray().flatten().toFloatArray()

            batch.draw(Assets.slimeTexture, actualVertices, 0, actualVertices.size)
        }
        if(slimer.ropeySlimey.any()) {
            val selectedPair = slimer.ropeySlimey.selectedItem.triangle
            shapeDrawer.filledCircle(selectedPair.first.position, 1f, Color.BLUE)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val sprite = spriteComponent.get(entity)
        sprite.sprite.draw(batch)
        //shapeDrawer.filledCircle(sprite.sprite.x, sprite.sprite.y, .1f, sprite.color)
        if(slimerComponent.has(entity)) {
            renderSlimer(slimerComponent.get(entity))
        }
    }
}
