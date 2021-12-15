package core.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils.radiansToDegrees
import core.ecs.AshleyMappers.bodyComponent
import core.ecs.AshleyMappers.spriteComponent
import core.ecs.components.BodyComponent
import core.ecs.components.SpriteComponent
import ktx.ashley.allOf

class PhysicsEntityUpdateSystem : IteratingSystem(allOf(BodyComponent::class, SpriteComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val body = bodyComponent.get(entity).body!!
        val sprite = spriteComponent.get(entity).sprite
        sprite.setOriginBasedPosition(body.position.x, body.position.y)
        sprite.rotation = body.angle * radiansToDegrees
    }
}

