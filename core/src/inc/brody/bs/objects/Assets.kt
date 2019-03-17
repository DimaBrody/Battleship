package inc.brody.bs.objects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

object Assets {
    var batch: SpriteBatch? = null

    //TEXTURES
    lateinit var cell: Texture
    lateinit var cell_down: Texture
    lateinit var xmark: Texture
    lateinit var zero: Texture
    lateinit var white: Texture
    lateinit var restart: Texture
    lateinit var enemyCell: Texture

    //BUTTON STYLE
    lateinit var bs : Button.ButtonStyle

    //FONT
    val ftfg = FreeTypeFontGenerator(Gdx.files.internal("co.ttf"))
    val fp = FreeTypeFontGenerator.FreeTypeFontParameter()
    lateinit var font: BitmapFont

    fun load(){
        batch = SpriteBatch()

        cell = Texture(Gdx.files.internal("cell.png"))
        cell_down = Texture(Gdx.files.internal("cell_down.png"))
        xmark = Texture(Gdx.files.internal("xmark.png"))
        zero = Texture(Gdx.files.internal("zero.png"))
        white = Texture(Gdx.files.internal("white.png"))
        restart = Texture(Gdx.files.internal("restart.png"))
        enemyCell = Texture(Gdx.files.internal("cellEnemy.png"))

        fp.size = 60
        fp.color = Color.BLACK

        font = ftfg.generateFont(fp)

        bs = Button.ButtonStyle(
                TextureRegionDrawable(TextureRegion(cell)),
                TextureRegionDrawable(TextureRegion(cell)),
                TextureRegionDrawable(TextureRegion(cell))
        )
    }

    fun dispose(){
        batch?.dispose()
    }
}