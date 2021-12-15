package core.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import core.ecs.components.BlobComponent
import ktx.ashley.allOf

class BlobbingSystem: IteratingSystem(allOf(BlobComponent::class).get()) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        TODO("Not yet implemented")
    }

}