package inc.brody.ship;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class EnemyCell extends Actor {
    private boolean isOk = false;
    private TextureRegion texture;
    public int[] coords;

    public EnemyCell(int[] coords){
        this.coords = coords;
        texture = new TextureRegion(Assets.cell);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(!isOk) batch.draw(texture,getX()+1280/2,getY()+720/2,getWidth(),getHeight());
    }

    public void destroyCell(){
        addAction(Actions.fadeOut(.5f,Interpolation.sine));
        isOk = true;
    }
}
