package core

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import core.ecs.components.BodyComponent
import core.ecs.components.CameraFollowComponent
import core.ecs.components.SlimerComponent
import core.ecs.components.SpriteComponent
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
        }
        engine.entity {
            with<BodyComponent> {
                this.body = body
            }
            with<SpriteComponent>()
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
            with<SpriteComponent>()
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
            circle(2f, vec2(0f, 0f)) {}
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

    fun createSlime(at: Vector2) {
        /**
         * Let's assume a cirlce with 12 sections, to keep it
         * reasonable.
         * x = cx + r * cos(a)
         * y = cy + r * sin(a)
         */
        val slimer = SlimerComponent()
        val numberOfPoints = 10
        val radius = 15f
        val angleShift = MathUtils.PI2 / numberOfPoints
        var currentAngle = 0f
        val theta = MathUtils.PI - angleShift / 2 - MathUtils.PI / 2
        val baseLength = 2 * radius * MathUtils.cos(theta)
        /**
         * 2b ⋅ cosθ
         */


        val centerBody = world.body {
            type = BodyDef.BodyType.DynamicBody
            position.set(at)
            fixedRotation = false
            circle(1f, at) {
                density = .1f
            }
        }
        slimer.centerBody = centerBody
        lateinit var previousBody: Body
        lateinit var currentBody: Body
        lateinit var firstBody: Body
        for (index in 0 until numberOfPoints) {
            //1. Calculate point location using simple trigonometry
            val x = at.x + radius * MathUtils.cos(currentAngle)
            val y = at.y + radius * MathUtils.sin(currentAngle)
            val vertex = vec2(x, y)

            currentBody = world.body {
                type = BodyDef.BodyType.DynamicBody
                position.set(at.x + vertex.x, at.y + vertex.y)
                fixedRotation = false
                circle(1f, vec2(0f, 0f)) {
                    density = .1f
                }
            }
            slimer.outershell.add(currentBody)
            centerBody.distanceJointWith(currentBody) {
                this.length = radius
                this.frequencyHz = 0.5f
                this.dampingRatio = 0.1f
            }
            if(index == 0) {
                firstBody = currentBody
            }
            if(index > 0) {
                previousBody.distanceJointWith(currentBody) {
                    this.length = baseLength
                    this.frequencyHz = 1f
                    this.dampingRatio = 0.1f
                }
            }
            if(index == numberOfPoints - 1) {
                firstBody.distanceJointWith(currentBody) {
                    this.length = baseLength
                    this.frequencyHz = 1f
                    this.dampingRatio = 0.1f
                }
            }
            previousBody = currentBody
            currentAngle += angleShift
        }

        val entity = engine.createEntity()
        entity.add(slimer)
        entity.add(SpriteComponent())
        entity.add(CameraFollowComponent())
        entity.add(BodyComponent().apply { body = centerBody })
        engine.addEntity(entity)
    }
}