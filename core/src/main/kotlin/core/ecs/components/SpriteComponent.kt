package core.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Pool

class SpriteComponent : Component, Pool.Poolable {
    var sprite: Sprite = Sprite()
    override fun reset() {
        sprite = Sprite()
    }

}
