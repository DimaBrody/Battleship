package inc.brody.bs.functions

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import inc.brody.bs.objects.Constants

fun setTableFillParent(vararg tables: Table) = tables.forEach { it.setFillParent(true) }


//Custom for loops
fun funcForInBlock(callback: (Int, Int) -> Unit) {
    for (i in -1..1) {
        for (j in -1..1) {
            callback(i, j)
        }
    }
}


fun funcForToTen(callback: (Int, Int) -> Unit) {
    for (u in 0 until 10) {
        for (j in 0 until 10) {
            callback(u, j)
        }
    }
}

fun Actor.moveByStartAnimation(height: Float, duration: Float) = addAction(Actions.moveBy(0f,height,duration, Interpolation.sine))

fun checkBounds(coords: IntArray, q: Int, a: Int): Boolean =
        coords[0] + q in 0..9 && coords[1] + a in 0..9

fun checkClickBounds(coords: IntArray, i: Int, j: Int): Boolean =
        coords[0] + i in 0..9 && coords[1] + j in 0..9 && !(i == 0 && j == 0)

fun initInputProcessor(stage: Stage) {
    val im = InputMultiplexer()
    im.addProcessor(object : InputAdapter() {
        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false
    })
    im.addProcessor(stage)
    Gdx.input.inputProcessor = im
}

fun moveTables(vararg tables: Table, height: Float = Constants.HEIGHT, duration: Float = .5f)
        = tables.forEach { it.moveByStartAnimation(height, duration) }


fun rowTables(vararg tables: Table){ tables.forEach { it.row() } }


//ZEROES
val zeroes: Array<IntArray>
    get() = arrayOf(
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    )