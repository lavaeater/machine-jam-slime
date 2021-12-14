package core.ecs.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.physics.box2d.World

class PhysicsUpdateSystem(
    private val world: World) : EntitySystem(0) {

    private val velIters = 8
    private val posIters = 3
    private val timeStep = 1/60f

    var accumulator = 0f

    override fun update(deltaTime: Float) {
        val ourTime = deltaTime.coerceAtMost(timeStep * 2)
        accumulator += ourTime
        while (accumulator > timeStep) {
            world.step(timeStep, velIters, posIters)
            accumulator -= ourTime
        }
    }
}