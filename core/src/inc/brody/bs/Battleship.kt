package inc.brody.bs

import com.badlogic.gdx.Game
import inc.brody.bs.objects.Assets
import inc.brody.bs.screens.GameScreen

class Battleship : Game() {

    override fun create() {
        Assets.load()
        setScreen(GameScreen(this))
    }

    override fun dispose() {
        Assets.dispose()
    }

}
