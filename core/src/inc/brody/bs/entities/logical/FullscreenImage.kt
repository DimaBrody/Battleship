package inc.brody.bs.entities.logical

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import inc.brody.bs.objects.Constants

class FullscreenImage(texture: Texture,
                      xpos : Float = 0f,
                      ypos: Float = 0f,
                      cwidth: Float = Constants.WIDTH,
                      cheight : Float = Constants.HEIGHT,
                      alpha: Float? = null,
                      listener: ()->Unit = {}) : Image(texture){

    fun initActions(vararg actions: Action) = Actions.sequence(*actions)

    init {
        width = cwidth
        height = cheight
        x = xpos
        y = ypos
        alpha?.let { color.a = it }

        addListener {
            listener()
            true
        }
    }
}