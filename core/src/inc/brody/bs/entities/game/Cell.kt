package inc.brody.bs.entities.game

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Button
import inc.brody.bs.objects.Assets

class Cell(val coords: IntArray) : Button(Assets.bs) {
    var isTouched = false

    fun destroyCell()
        = addAction(Actions.fadeOut(.5f, Interpolation.sine))

}