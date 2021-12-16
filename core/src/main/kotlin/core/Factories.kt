package core

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint
import core.ecs.components.BodyComponent
import core.ecs.components.CameraFollowComponent
import core.ecs.components.SlimerComponent
import core.ecs.components.SpriteComponent
import injection.GameConstants.outerShellDamp
import injection.GameConstants.outerShellHz
import injection.GameConstants.spokeDamp
import injection.GameConstants.spokeHz
import ktx.ashley.entity
import ktx.ashley.with
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.circle
import ktx.box2d.distanceJointWith
import ktx.math.vec2
import kotlin.math.sqrt

object Factories {
    fun platform(at: Vector2, width: Float, height: Float) {
        val body = world.body {
            type = BodyDef.BodyType.StaticBody
            position.set(at)
            box(width, height) {}
            userData = "PLATFORM"
        }
        engine.entity {
            with<BodyComponent> {
                this.body = body
            }
        }
    }

    fun obstacle(at: Vector2) {
        val body = world.body {
            type = BodyDef.BodyType.StaticBody
            position.set(at)
            box(2f, 2f) {}
            userData = "PLATFORM"
        }
        engine.entity {
            with<BodyComponent> {
                this.body = body
            }
            with<SpriteComponent> {
                sprite = Assets.obstacleSprite()
            }
        }
    }

    fun ball(at: Vector2) : Body {
        val body = world.body {
            type = BodyDef.BodyType.DynamicBody
            position.set(at)
            circle(1f) {
                density = .1f
            }
        }
        engine.entity {
            with<BodyComponent> {
                this.body = body
            }
        }
        return body
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

        val centerBody = world.body {
            type = BodyDef.BodyType.DynamicBody
            position.set(at)
            circle(15f, vec2(0f, 0f)) {}
        }
        lateinit var previousBody: Body
        lateinit var firstBody: Body
        for((index, vertex) in vertices.withIndex()) {
            val currentBody = world.body {
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
        }
    }

    fun createSlime(at: Vector2, radius: Float, numberOfPoints: Int, nodeRadius:Float) {
        /**
         * Let's assume a cirlce with 12 sections, to keep it
         * reasonable.
         * x = cx + r * cos(a)
         * y = cy + r * sin(a)
         */
        val slimer = SlimerComponent()
        val angleShift = MathUtils.PI2 / numberOfPoints
        var currentAngle = 0f
        val theta = MathUtils.PI - angleShift / 2 - MathUtils.PI / 2
        val baseLength = 2 * radius * MathUtils.cos(theta)
        /**
         * 2b ⋅ cosθ
         */

        val centerBody = createSlimeNode(at, nodeRadius)
        slimer.centerBody = centerBody
        lateinit var previousBody: Body
        lateinit var currentBody: Body
        lateinit var firstBody: Body
        for (index in 0 until numberOfPoints) {
            //1. Calculate point location using simple trigonometry
            val x = at.x + radius * MathUtils.cos(currentAngle)
            val y = at.y + radius * MathUtils.sin(currentAngle)
            val vertex = vec2(x, y)

            currentBody = createSlimeNode(vec2(at.x + vertex.x, at.y + vertex.y), nodeRadius)
            slimer.outershell.add(currentBody)
            slimer.allJoints.add(centerBody.distanceJointWith(currentBody) {
                this.length = radius
                this.frequencyHz = spokeHz
                this.dampingRatio = spokeDamp
                collideConnected = false
            })
            if(index == 0) {
                firstBody = currentBody
            }
            if(index > 0) {
                slimer.allSections.add(Triple(centerBody, previousBody, currentBody))
                slimer.outerPairs.add(Pair(previousBody, currentBody))
                slimer.allJoints.add(previousBody.distanceJointWith(currentBody) {
                    this.length = baseLength
                    this.frequencyHz = outerShellHz
                    this.dampingRatio = outerShellDamp
                    collideConnected = false
                })
            }
            if(index == numberOfPoints - 1) {
                slimer.allSections.add(Triple(centerBody, firstBody, currentBody))
                slimer.outerPairs.add(Pair(currentBody, firstBody))
                slimer.allJoints.add(firstBody.distanceJointWith(currentBody) {
                    this.length = baseLength
                    this.frequencyHz = outerShellHz
                    this.dampingRatio = outerShellDamp
                    collideConnected = false
                })
            }
            previousBody = currentBody
            currentAngle += angleShift
        }

        var entity = engine.createEntity()
        entity.add(slimer)
        entity.add(CameraFollowComponent())
        entity.add(SpriteComponent().apply { sprite = Assets.dummySprite })
        entity.add(BodyComponent().apply { body = centerBody })
        engine.addEntity(entity)
        centerBody.userData = entity

        for(body in slimer.outershell) {
            createSlimeEntity(body)
        }
    }

    fun createSlimeEntity(body: Body) : Entity {
        val entity = engine.entity {
            with<BodyComponent> {
                this.body = body
            }
        }
        body.userData = entity
        return entity
    }

    fun createSlimeNode(at: Vector2, radius: Float) : Body {
        return world.body {
            type = BodyDef.BodyType.DynamicBody
            position.set(at)
            fixedRotation = false
            circle(radius) {
                density = .1f
            }
        }
    }
}

fun Body.outershellJointWith(body: Body, length: Float) : DistanceJoint {
    return this.distanceJointWith(body) {
        this.length = length
        this.frequencyHz = 1f
        this.dampingRatio = 0.1f
    }
}