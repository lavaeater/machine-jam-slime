package core.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool

class BlobComponent: Component, Pool.Poolable {
    val blobs = mutableListOf<Body>()
    override fun reset() {
        blobs.clear()
    }

}