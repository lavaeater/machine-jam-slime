package core.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils.radiansToDegrees
import core.ecs.components.BlobComponent
import core.ecs.components.BodyComponent
import core.ecs.components.SpriteComponent
import core.ecs.systems.AshleyMappers.bodyComponent
import core.ecs.systems.AshleyMappers.spriteComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

object AshleyMappers {
    val bodyComponent = mapperFor<BodyComponent>()
    val spriteComponent = mapperFor<SpriteComponent>()
}

class PhysicsEntityUpdateSystem : IteratingSystem(allOf(BodyComponent::class, SpriteComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val body = bodyComponent.get(entity).body!!
        val sprite = spriteComponent.get(entity).sprite
        sprite.setOriginBasedPosition(body.position.x, body.position.y)
        sprite.rotation = body.angle * radiansToDegrees
    }
}

class BlobbingSystem: IteratingSystem(allOf(BlobComponent::class).get()) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        TODO("Not yet implemented")
    }

}