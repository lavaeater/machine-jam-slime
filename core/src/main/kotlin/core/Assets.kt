package core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonRegion
import com.badlogic.gdx.graphics.g2d.TextureRegion

object Assets {
    val assetManager = AssetManager()
    val slimeTexture by lazy { Texture(Gdx.files.internal("textures/slime.png")) }
//    val slimePoly by lazy {
//        PolygonRegion(slimeTexture,)
//    }
}