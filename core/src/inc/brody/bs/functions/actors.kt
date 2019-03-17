package inc.brody.bs.functions

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import inc.brody.bs.entities.logical.LastLabel
import inc.brody.bs.objects.Constants

fun LastLabel.setLastScreenLabel(x: Float = 0f,
                                 y: Float = 0f,
                                 width: Float = Constants.WIDTH,
                                 height: Float = Constants.HEIGHT,
                                 alignment : Int = Align.center,
                                 alpha: Float = 1f,
                                 isWrap: Boolean? = null,
                                 isFontScale: Float? = null,
                                 listener: ()->Unit = {}) : LastLabel {
    setBounds(x,y,width,height)
    setAlignment(alignment)
    addListener {
        listener()
        true
    }
    isWrap?.let { setWrap(it) }
    isFontScale?.let { setFontScale(isFontScale,isFontScale) }
    color.a = alpha
    return this
}

fun Stage.setActors(vararg actor: Actor) = actor.forEach { addActor(it) }

fun Stage.setOwnAction(action: ()->Boolean) = addAction(object : Action() {
    override fun act(delta: Float): Boolean {
        return action()
    }
})

fun Cell<out Actor>.setMeasure(measure: Float) : Cell<out Actor> {
    width(measure)
    height(measure)
    return this
}