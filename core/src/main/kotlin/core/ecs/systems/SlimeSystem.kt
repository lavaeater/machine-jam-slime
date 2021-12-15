package core.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import core.ControlObject
import core.Factories.ball
import core.Factories.createSlimeEntity
import core.Factories.createSlimeNode
import core.ecs.AshleyMappers.slimerComponent
import core.ecs.AshleyMappers.spriteComponent
import core.ecs.components.SlimeRope
import core.ecs.components.SlimerComponent
import core.world
import injection.GameConstants
import injection.GameConstants.outerShellDamp
import injection.GameConstants.outerShellHz
import injection.GameConstants.segmentLength
import ktx.ashley.allOf
import ktx.box2d.RayCast
import ktx.box2d.distanceJointWith
import ktx.box2d.jointWith
import ktx.box2d.rayCast
import ktx.math.component1
import ktx.math.vec2

class SlimeSystem : IteratingSystem(allOf(SlimerComponent::class).get()) {

    val testBody by lazy {
        ball(vec2(0f, 0f))
    }


    override fun processEntity(entity: Entity, deltaTime: Float) {
        val slimer = slimerComponent.get(entity)
        val endVec = slimer.centerBody.position.cpy().add(ControlObject.aimVector.cpy().scl(15f))

        slimer.outershell.sortBy { it.position.dst(endVec) }
        val closest = slimer.outershell.take(2)
        for (close in closest) {
            val entity = close.userData as Entity
            val spriteComponent = spriteComponent.get(entity)
            spriteComponent.color = Color.GREEN
        }

        if (ControlObject.leftTrigger && ControlObject.leftTriggerEnabled) {
            ControlObject.leftTriggerEnabled = false
            /**
             * 1. Create a new body / entity and then add sections of joints out from it...
             *
             * First body should be at endVec
             */
            val rope = SlimeRope(mutableMapOf(), mutableListOf())
            slimer.ropeySlimey.add(rope)
            val firstBody = createSlimeNode(endVec, .5f)
            val entity = createSlimeEntity(firstBody)
            rope.nodes[firstBody] = entity
            for (b in closest) {
                rope.joints.add(b.distanceJointWith(firstBody) {
                    this.length = b.position.dst(firstBody.position)
                    this.frequencyHz = GameConstants.outerShellHz
                    this.dampingRatio = GameConstants.outerShellDamp
                })
            }

            /**
             * Then we ray cast from the new node to... mouseposition, of course
             */
            val rayCastEnd = slimer.centerBody.position.cpy().add(ControlObject.aimVector.cpy().scl(200f))
            world.rayCast(
                startX = firstBody.position.x,
                startY = firstBody.position.y,
                endX = rayCastEnd.x,
                endY = rayCastEnd.y
            ) { fixture, point, normal, fraction ->

                /**
                 *
                 * Now, create a slimey rope of string between these.
                 */
                val endPosition = point.cpy()
                val distance = firstBody.position.dst(endPosition)
                val numberOfSegments = (distance / segmentLength).toInt() / 2

                val segmentVector = endPosition.cpy().sub(firstBody.position).nor().scl(segmentLength)


                lateinit var currentBody: Body
                var previousBody = firstBody
                for (segment in 0 until numberOfSegments) {
                    val newPos = firstBody.position.cpy().add(segmentVector)
                    currentBody = createSlimeNode(newPos, .5f)
                    val currentEntity = createSlimeEntity(currentBody)
                    rope.nodes[currentBody] = currentEntity
                    rope.joints.add(currentBody.distanceJointWith(previousBody) {
                        length = segmentLength
                        frequencyHz = outerShellHz
                        dampingRatio = outerShellDamp
                        collideConnected = false
                    })
                    previousBody = currentBody
                    if (segment == numberOfSegments - 1) {
                        rope.joints.add(currentBody.distanceJointWith(fixture.body) {
                            localAnchorB.set(fixture.body.getLocalPoint(endPosition))
                            length = 1f
                            frequencyHz = 50f
                            dampingRatio = 1f
                            collideConnected = false
                        })
                    }
                }

                RayCast.CONTINUE
            }

        }

        for (body in slimer.outershell.filterNot { closest.contains(it) }) {
            val entity = body.userData as Entity
            val spriteComponent = spriteComponent.get(entity)
            spriteComponent.color = Color.RED
        }
        if (ControlObject.directionVector != Vector2.Zero) {
            /**
             * Take the directionvector and apply it as a force on the normal
             * of the body in relation to the center point. This might
             * create a rotating thing
             *
             * Or, here's a thing, just start rotating all of the balls,
             * that works too, right?
             */

            for (body in slimer.outershell) {
                body.applyTorque(ControlObject.directionVector.x * 100f * deltaTime, true)
            }
        }
    }
}