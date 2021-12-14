package core.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import core.ecs.components.BodyComponent
import core.ecs.components.CameraFollowComponent
import ktx.ashley.allOf

class CameraFollowSystem(private val camera: OrthographicCamera): IteratingSystem(
    allOf(
        CameraFollowComponent::class,
        BodyComponent::class
    ).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val body = AshleyMappers.bodyComponent.get(entity).body!!
        camera.position.set(body.position.x, body.position.y, 0f)
        camera.update(true)
    }
}