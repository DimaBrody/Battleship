package inc.brody.bs.entities.game

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import inc.brody.bs.objects.Assets
import inc.brody.bs.objects.Constants

class EnemyCell(val coords: IntArray) : Actor() {
    private val texture = TextureRegion(Assets.enemyCell)
    var isOk = false

    fun destroyCell(){ isOk = true }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if(!isOk) batch?.draw(texture,x,y,width,height)
    }
}