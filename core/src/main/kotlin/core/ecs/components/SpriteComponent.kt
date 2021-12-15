package core.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Pool

class SpriteComponent : Component, Pool.Poolable {
    var sprite: Sprite = Sprite()
    var color: Color = Color.WHITE
    override fun reset() {
        sprite = Sprite()
        color = Color.WHITE
    }

}
