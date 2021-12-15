package core

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.MathUtils.*
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Pool
import ktx.ashley.entity
import ktx.box2d.body
import ktx.box2d.circle
import ktx.box2d.distanceJointWith
import ktx.math.vec2

class SlimerComponent: Component, Pool.Poolable {
    lateinit var centerBody: Body
    val outershell = mutableListOf<Body>()
    override fun reset() {
        if(::centerBody.isInitialized) {
            world.destroyBody(centerBody)
        }
        outershell.clear()
    }
}

fun buildSlimeCircle(at: Vector2) {
    /**
     * Let's assume a cirlce with 12 sections, to keep it
     * reasonable.
     * x = cx + r * cos(a)
     * y = cy + r * sin(a)
     */
    val slimer = SlimerComponent()
    val numberOfPoints = 20
    val radius = 15f
    val angleShift = PI2 / numberOfPoints
    var currentAngle = 0f
    val theta = PI - angleShift / 2 - PI / 2
    val baseLength = 2 * radius * cos(theta)
    /**
     * 2b ⋅ cosθ
     */


    val centerBody = world.body {
        type = BodyDef.BodyType.DynamicBody
        position.set(at)
        circle(1f, at) {}
    }
    slimer.centerBody = centerBody
    lateinit var previousBody: Body
    lateinit var currentBody: Body
    lateinit var firstBody: Body
    for (index in 0 until numberOfPoints) {
        //1. Calculate point location using simple trigonometry
        val x = at.x + radius * cos(currentAngle)
        val y = at.y + radius * sin(currentAngle)
        val vertex = vec2(x, y)

        currentBody = world.body {
            type = BodyDef.BodyType.DynamicBody
            position.set(at.x + vertex.x, at.y + vertex.y)
            circle(1f, vec2(0f, 0f)) {}
        }
        slimer.outershell.add(currentBody)
        centerBody.distanceJointWith(currentBody) {
            this.length = radius
            this.frequencyHz = 1f
            this.dampingRatio = 0.3f
        }
        if(index == 0) {
            firstBody = currentBody
        }
        if(index > 0) {
            previousBody.distanceJointWith(currentBody) {
                this.length = baseLength
                this.frequencyHz = 25f
                this.dampingRatio = 0.9f
            }
        }
        if(index == numberOfPoints - 1) {
            firstBody.distanceJointWith(currentBody) {
                this.length = baseLength
                this.frequencyHz = 25f
                this.dampingRatio = 0.9f
            }
        }
        previousBody = currentBody
        currentAngle += angleShift
    }

    val entity = engine.createEntity()
    entity.add(slimer)
}