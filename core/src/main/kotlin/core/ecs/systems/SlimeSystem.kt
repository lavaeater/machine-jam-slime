package core.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import core.ControlObject
import core.Factories.ball
import core.ecs.AshleyMappers.slimerComponent
import core.ecs.components.SlimerComponent
import ktx.ashley.allOf
import ktx.math.vec2

class SlimeSystem: IteratingSystem(allOf(SlimerComponent::class).get()) {

    val testBody by lazy {
        ball(vec2(0f,0f))
    }


    override fun processEntity(entity: Entity, deltaTime: Float) {
        if(ControlObject.directionVector != Vector2.Zero) {
            /**
             * Take the directionvector and apply it as a force on the normal
             * of the body in relation to the center point. This might
             * create a rotating thing
             *
             * Or, here's a thing, just start rotating all of the balls,
             * that works too, right?
             */
            val slimer = slimerComponent.get(entity)
            for (body in slimer.outershell) {
                body.applyTorque(ControlObject.directionVector.x * 100f * deltaTime, true)
            }
        }
    }
}