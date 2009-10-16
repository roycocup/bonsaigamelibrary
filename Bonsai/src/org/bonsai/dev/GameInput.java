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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;

public class GameInput extends GameComponent implements MouseListener,
		MouseMotionListener, KeyListener, FocusListener {

	public GameInput(final Game g) {
		super(g);
	}

	// Mouse
	private LinkedList<Integer> mousePressed = new LinkedList<Integer>();
	private LinkedList<Integer> mouseDown = new LinkedList<Integer>();
	private int mouseX = 0;
	private int mouseY = 0;

	public void mouseClicked(final MouseEvent e) {
	}

	public final void mousePressed(final MouseEvent e) {
		int button = e.getButton();
		if (!mouseDown.contains(button)) {
			mouseDown.add(button);
			mousePressed.add(button);
		}
	}

	public final void mouseReleased(final MouseEvent e) {
		int button = e.getButton();
		mouseDown.remove(Integer.valueOf(button));
		if (mousePressed.contains(button)) {
			mousePressed.remove(Integer.valueOf(button));
		}
	}

	public void mouseEntered(final MouseEvent e) {
	}

	public final void mouseExited(final MouseEvent e) {
		mouseDown.clear();
		mousePressed.clear();
	}

	public final void mouseDragged(final MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public final void mouseMoved(final MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public final void clearMouse() {
		mousePressed.clear();
	}

	// Keyboard
	private LinkedList<Integer> keysPressed = new LinkedList<Integer>();
	private LinkedList<Integer> keysDown = new LinkedList<Integer>();
	private LinkedList<Integer> keysRemove = new LinkedList<Integer>();

	public void keyTyped(final KeyEvent e) {
	}

	public final void keyPressed(final KeyEvent e) {
		int key = e.getKeyCode();
		if (!keysDown.contains(key)) {
			keysDown.add(key);
			keysPressed.add(key);
		}
		e.consume();
	}

	public final void keyReleased(final KeyEvent e) {
		int key = e.getKeyCode();

		// Fix bugged PRINTSCREEN key event, only fires on keyReleased
		if (key == java.awt.event.KeyEvent.VK_PRINTSCREEN) {
			keysPressed.add(key);
		}
		keysRemove.add(key);
		e.consume();
	}

	public final void clearKeys() {
		for (int i = 0; i < keysRemove.size(); i++) {
			int key = keysRemove.get(i);
			keysDown.remove(Integer.valueOf(key));
			if (keysPressed.contains(key)) {
				keysPressed.remove(Integer.valueOf(key));
			}
		}
		keysRemove.clear();
		keysPressed.clear();
	}

	// Focus
	public final void focusGained(final FocusEvent e) {
		game.setFocused(true);
		if (game.isPausedOnFocus()) {
			game.pause(false);
		}
	}

	public final void focusLost(final FocusEvent e) {
		game.setFocused(false);
		if (game.isPausedOnFocus()) {
			game.pause(true);
		}
		keysDown.clear();
		keysPressed.clear();
		mouseDown.clear();
		mousePressed.clear();
	}

	// Getters
	public final int mouseX() {
		return mouseX / game.scale();
	}

	public final int mouseY() {
		return mouseY / game.scale();
	}

	public final boolean mouseDown(final int button) {
		return mouseDown.contains(button);
	}

	public final boolean mousePressed(final int button) {
		return mousePressed.contains(button);
	}

	public final boolean keyDown(final int key) {
		return keysDown.contains(key);
	}

	public final boolean keyPressed(final int key) {
		return keysPressed.contains(key);
	}
}
