package org.bonsai.dev;

public class GameComponent {
	protected Game game;

	public GameComponent(Game g) {
		game = g;
	}
	
	public long getTime() {
		return game.getTime();
	}
}
