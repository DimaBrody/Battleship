package inc.brody.ship;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Assets {
    public static SpriteBatch batch;

    public static Texture cell;
    public static Texture cell_down;
    public static Texture xmark;
    public static Texture zero;
    public static Texture white;
    public static Texture restart;
    public static Texture enemyCell;

    public static Button.ButtonStyle bs;

    public static FreeTypeFontGenerator ftfg = new FreeTypeFontGenerator(Gdx.files.internal("co.ttf"));
    public static FreeTypeFontGenerator.FreeTypeFontParameter fp = new FreeTypeFontGenerator.FreeTypeFontParameter();
    public static BitmapFont font;


    public static void load(){
        batch = new SpriteBatch();

        cell = new Texture(Gdx.files.internal("cell.png"));
        cell_down = new Texture(Gdx.files.internal("cell_down.png"));
        xmark = new Texture(Gdx.files.internal("xmark.png"));
        zero = new Texture(Gdx.files.internal("zero.png"));
        white = new Texture(Gdx.files.internal("white.png"));
        restart = new Texture(Gdx.files.internal("restart.png"));
        enemyCell = new Texture(Gdx.files.internal("cellEnemy.png"));

        fp.size = 60;
        fp.color  = Color.BLACK;

        font = ftfg.generateFont(fp);

        bs = new Button.ButtonStyle(new TextureRegionDrawable(new TextureRegion(cell)),new TextureRegionDrawable(new TextureRegion(cell)),new TextureRegionDrawable(new TextureRegion(cell)));
    }

    public static void dispose(){
        if(batch != null) batch.dispose();
    }
}
