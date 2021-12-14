package core.box2d

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Joint
import com.badlogic.gdx.physics.box2d.World

/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/** A distance joint constrains two points on two bodies to remain at a fixed distance from each other. You can view this as a
 * massless, rigid rod.  */
class DistanceJoint(world: World?, joint: org.jbox2d.dynamics.joints.DistanceJoint) :
    Joint(world, joint) {
    var joint: org.jbox2d.dynamics.joints.DistanceJoint
    var localAnchorA = Vector2()
    var localAnchorB = Vector2()
    fun getLocalAnchorA(): Vector2 {
        val localAnchor: Vec2 = joint.getLocalAnchorA()
        localAnchorA[localAnchor.x] = localAnchor.y
        return localAnchorA
    }

    fun getLocalAnchorB(): Vector2 {
        val localAnchor: Vec2 = joint.getLocalAnchorB()
        localAnchorB[localAnchor.x] = localAnchor.y
        return localAnchorB
    }
    /** Set/get the natural length. Manipulating the length can lead to non-physical behavior when the frequency is zero.  */
    /** Set/get the natural length. Manipulating the length can lead to non-physical behavior when the frequency is zero.  */
    var length: Float
        get() = joint.getLength()
        set(length) {
            joint.setLength(length)
        }
    /** Set/get frequency in Hz.  */
    /** Set/get frequency in Hz.  */
    var frequency: Float
        get() = joint.getFrequency()
        set(hz) {
            joint.setFrequency(hz)
        }
    /** Set/get damping ratio.  */
    /** Set/get damping ratio.  */
    var dampingRatio: Float
        get() = joint.getDampingRatio()
        set(ratio) {
            joint.setDampingRatio(ratio)
        }

    init {
        this.joint = joint
    }
}