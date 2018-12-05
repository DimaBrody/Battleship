package inc.brody.ship;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

public class Cell extends Button {
    public boolean isTouched = false;
    public int[] coords;

    public Cell(int[] coords){
        super(Assets.bs);
        this.coords = coords;

    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void destroyCell(){
        addAction(Actions.fadeOut(.5f,Interpolation.sine));
    }
}
