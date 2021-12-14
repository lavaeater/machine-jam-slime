package core.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool

class CameraFollowComponent: Component, Pool.Poolable {
    override fun reset() {

    }

}

class BodyComponent: Component, Pool.Poolable {
    var body: Body? = null
    override fun reset() {
        body = null
    }
}

class SpriteComponent : Component, Pool.Poolable {
    var sprite: Sprite = Sprite()
    override fun reset() {
        sprite = Sprite()
    }

}
