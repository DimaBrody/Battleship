package inc.brody.bs.entities.logical

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import inc.brody.bs.objects.Constants

//Singleton class with custom actions
object CustomAction {

    private fun moveAwayAction() = Actions.moveBy(Constants.GETAWAY, Constants.GETAWAY)!!

    fun enemyTurnAction(vararg delays: Float,
                        firstAction: () -> Unit,
                        secondAction : ()->Unit,
                        thirdAction: ()->Unit) =
            Actions.sequence(
                    Actions.delay(delays[0]),
                    object : Action() {
                        override fun act(delta: Float): Boolean {
                            firstAction()
                            return true
                        }
                    },
                    Actions.delay(delays[1]),
                    object : Action(){
                        override fun act(delta: Float): Boolean {
                            secondAction()
                            return true
                        }
                    },
                    Actions.delay(delays[2]),
                    object : Action(){
                        override fun act(delta: Float): Boolean {
                            thirdAction()
                            return true
                        }
                    }
            )!!

    fun moveAwayAlphaAction(alpha: Float = 0f) = Actions.sequence(
            Actions.alpha(alpha,.5f, Interpolation.sine),
            moveAwayAction()
    )!!

    private fun foreverSequenceAction(vararg actions: Action) =
            Actions.forever(
                    Actions.sequence(*actions)
            )!!

    private fun scaleAction(scale: Float) =
            Actions.scaleBy(scale,scale,.5f, Interpolation.sine)!!

    fun foreverScalingAction(scale: Float) =
        foreverSequenceAction(
                scaleAction(scale),
                scaleAction(-scale)
        )

    fun sequenceDelayOwnAction(duration: Float,action: ()->Boolean) =
            Actions.sequence(
                    Actions.delay(duration),
                    object : Action(){
                        override fun act(delta: Float): Boolean {
                            return action()
                        }
                    }
            )!!

    fun parallelFading(x: Float, y: Float,a: Float = 1f) =
            Actions.parallel(
                    Actions.alpha(a,.5f, Interpolation.sine),
                    Actions.moveTo(x,y)
            )!!

}