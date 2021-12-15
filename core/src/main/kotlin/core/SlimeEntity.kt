package core

import com.badlogic.gdx.math.MathUtils.*
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import ktx.box2d.body
import ktx.box2d.circle
import ktx.box2d.distanceJointWith
import ktx.math.vec2

class SlimeEntity {
}


/**
 * How do we build a nice little slime entity?
 *
 * Lets consider a slime as a sum of parts. The smallest part
 * would be a simple vertex. The next part up from that would be a...
 *
 * Lets say triangle, for now.
 *
 * So, a section is a triangle, and all sections can and will share vertices
 * with each other.
 */
class SlimeTriangle(val points: Array<Float>) {
    init {
        if(points.size == 6) {
        }
    }

}

fun buildSlimeCircle() {
    /**
     * Let's assume a cirlce with 12 sections, to keep it
     * reasonable.
     * x = cx + r * cos(a)
     * y = cy + r * sin(a)
     */
    val numberOfPoints = 20
    val centerPoint = vec2()
    val radius = 5f
    val angleShift = PI2 / numberOfPoints
    var currentAngle = 0f
    lateinit var centerBody: Body
    lateinit var previousBody: Body
    lateinit var currentBody: Body
    for (index in 0 until numberOfPoints) {
        //1. Calculate point location using simple trigonometry
        val x = centerPoint.x + radius * cos(currentAngle)
        val y = centerPoint.x + radius * cos(currentAngle)



        currentBody = world.body {
            type = BodyDef.BodyType.DynamicBody
            position.set(at.x + vertex.x, at.y + vertex.y)
            circle(2f, vec2(0f, 0f)) {}
        }
        centerBody.distanceJointWith(currentBody) {
            this.length = l
            this.frequencyHz = .5f
            this.dampingRatio = 0.05f
        }
        if(index == 0) {
            firstBody = currentBody
        }
        if(index > 0) {
            previousBody.distanceJointWith(currentBody) {
                this.length = l
                this.frequencyHz = .5f
                this.dampingRatio = 0.05f
            }
        }
        if(index == vertices.lastIndex) {
            firstBody.distanceJointWith(currentBody) {
                this.length = l
                this.frequencyHz = .5f
                this.dampingRatio = 0.05f
            }
        }
        previousBody = currentBody
        currentAngle += angleShift
    }
}