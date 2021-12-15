package core.ecs

import core.ecs.components.BodyComponent
import core.ecs.components.SpriteComponent
import ktx.ashley.mapperFor

object AshleyMappers {
    val bodyComponent = mapperFor<BodyComponent>()
    val spriteComponent = mapperFor<SpriteComponent>()
}