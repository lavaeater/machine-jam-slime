package core

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import core.ecs.components.BodyComponent
import core.ecs.components.CameraFollowComponent
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
            with<CameraFollowComponent>()
            with<SpriteComponent>()
        }
    }

    fun ball(at: Vector2) {
        val body = world.body {
            type = BodyDef.BodyType.DynamicBody
            position.set(at)
            circle(1f) {
            }
        }
        engine.entity {
            with<BodyComponent> {
                this.body = body
            }
            with<CameraFollowComponent>()
            with<SpriteComponent>()
        }
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
}