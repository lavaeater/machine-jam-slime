package core.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Joint
import com.badlogic.gdx.utils.Pool
import core.world

class SlimerComponent: Component, Pool.Poolable {
    lateinit var centerBody: Body
    val outershell = mutableListOf<Body>()
    val allJoints = mutableListOf<Joint>()
    val ropeySlimey = mutableListOf<SlimeRope>()
    val allSections = mutableListOf<Triple<Body, Body, Body>>()
    override fun reset() {
        if(::centerBody.isInitialized) {
            world.destroyBody(centerBody)
        }
        outershell.clear()
        ropeySlimey.clear()
    }
}

class SlimeRope(val nodes: MutableMap<Body, Entity>, val joints: MutableList<Joint>)