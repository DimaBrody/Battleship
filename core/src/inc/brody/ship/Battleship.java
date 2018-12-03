package inc.brody.ship;

import com.badlogic.gdx.Game;

public class Battleship extends Game {
	private Game game;

	@Override
	public void create () {
		Assets.load();
		setScreen(new GameScreen(game));
	}

	@Override
	public void dispose() {
		Assets.dispose();
	}
}
