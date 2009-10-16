/**
 *  This file is part of the Bonsai Game Library.
 *
 *  The Bonsai Game Library is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The Bonsai Game Library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with the Bonsai Game Library.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */

package org.bonsai.dev;

public class GameObject<G extends Game> {
	protected G game;
	protected GameAnimation animation;
	protected GameFont font;
	protected GameInput input;
	protected GameImage image;
	protected GameMenu menu;
	protected GameSound sound;
	protected GameTimer timer;

	public GameObject(final G g) {
		game = g;
		animation = game.animation;
		font = game.font;
		input = game.input;
		image = game.image;
		menu = game.menu;
		sound = game.sound;
		timer = game.timer;
	}

	public final long getTime() {
		return game.getTime();
	}
}
