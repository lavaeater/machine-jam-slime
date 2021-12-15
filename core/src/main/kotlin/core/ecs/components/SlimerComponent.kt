package core.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool
import core.world

class SlimerComponent: Component, Pool.Poolable {
    lateinit var centerBody: Body
    val outershell = mutableListOf<Body>()
    override fun reset() {
        if(::centerBody.isInitialized) {
            world.destroyBody(centerBody)
        }
        outershell.clear()
    }
}