package core.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool

class BodyComponent: Component, Pool.Poolable {
    var body: Body? = null
    override fun reset() {
        body = null
    }
}