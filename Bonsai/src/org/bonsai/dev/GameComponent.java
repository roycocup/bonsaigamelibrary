package org.bonsai.dev;

public class GameComponent {
	public Game game;

	public GameComponent(final Game g) {
		game = g;
	}
	
	public final long getTime() {
		return game.getTime();
	}
}
