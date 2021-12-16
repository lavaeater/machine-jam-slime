package core.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Joint
import com.badlogic.gdx.utils.Pool
import core.world

fun<T> selectedItemListOf(vararg items: T): SelectedItemList<T> {
    val list = SelectedItemList<T>()
    items.forEach { list.add(it) }
    return list
}

class SelectedItemList<T> : ArrayList<T>() {
    var selectedIndex: Int = 0
        set(value) {
            field = when {
                value < 0 -> this.lastIndex
                value > this.lastIndex -> 0
                else -> value
            }
        }
    val selectedItem get () = this[selectedIndex]
    fun nextItem() : T {
        selectedIndex++
        return selectedItem
    }
    fun previousItem() : T {
        selectedIndex--
        return selectedItem
    }

    override fun remove(element: T): Boolean {
        selectedIndex = 0
        return super.remove(element)
    }
}

class SlimerComponent: Component, Pool.Poolable {
    lateinit var centerBody: Body
    val outershell = mutableListOf<Body>()
    val outerPairs = selectedItemListOf<Pair<Body, Body>>()
    val allJoints = mutableListOf<Joint>()
    val ropeySlimey = selectedItemListOf<SlimeRope>()
    val allSections = mutableListOf<Triple<Body, Body, Body>>()

    override fun reset() {
        if(::centerBody.isInitialized) {
            world.destroyBody(centerBody)
        }
        outershell.clear()
        ropeySlimey.clear()
        outerPairs.clear()
    }
}

class SlimeRope(val nodes: MutableMap<Body, Entity>, val joints: MutableList<Joint>) {
    lateinit var triangle: Triple<Body, Body, Body>
    lateinit var anchorBodies: Pair<Body, Body>
}