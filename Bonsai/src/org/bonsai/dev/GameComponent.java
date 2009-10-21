package org.bonsai.dev;

public class GameComponent {
	public Game game;

	public GameComponent(final Game game) {
		this.game = game;
	}
	
	public final long getTime() {
		return game.getTime();
	}
}
