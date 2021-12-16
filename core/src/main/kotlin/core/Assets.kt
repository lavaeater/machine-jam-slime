package core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonRegion
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import elemental2.dom.Text
import injection.GameConstants

object Assets {
    val assetManager = AssetManager()

    val shapeTexture by lazy {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.drawPixel(0, 0)
        val texture = Texture(pixmap) //remember to dispose of later
        pixmap.dispose()
        texture
    }

    val dummySprite by lazy { Sprite(shapeTexture) }

    val slimeTexture by lazy { Texture(Gdx.files.internal("textures/slime.png")) }
    val obstacleTexture by lazy { Texture(Gdx.files.internal("textures/obstacle.png")) }
    fun obstacleSprite() : Sprite {
        return Sprite(obstacleTexture).apply {
            setOriginCenter()
            setScale(GameConstants.scale)
        }
    }
}