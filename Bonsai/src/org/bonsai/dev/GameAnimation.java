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

import java.util.HashMap;
import java.util.LinkedList;

public class GameAnimation extends GameComponent {
	private HashMap<String, Animation> animations = new HashMap<String, Animation>();
	protected LinkedList<Animation> animationList = new LinkedList<Animation>();

	public GameAnimation(Game g) {
		super(g);
	}

	public Animation add(String id, int frames[], int frameTime, boolean loop) {
		return new Animation(id, frames, frameTime, loop);
	}

	public void set(String id, int frame) {
		if (animations.containsKey(id)) {
			animations.get(id).set(frame);
		}
	}

	public int get(String id) {
		if (animations.containsKey(id)) {
			return animations.get(id).get();
		} else {
			return 0;
		}
	}

	public boolean delete(String id) {
		if (animations.containsKey(id)) {
			animationList.remove(animations.remove(id));
			return true;
		} else {
			return false;
		}
	}

	public class Animation {
		private int[] frames;
		private int pos;
		private int frameTime;
		private long lastTime;
		private boolean loop;

		public Animation(String id, int[] f, int time, boolean l) {
			frames = f;
			pos = 0;
			loop = l;
			frameTime = time;
			lastTime = -1;
			animations.put(id, this);
			animationList.add(this);
		}

		public void update() {
			if (lastTime == -1) {
				lastTime = getTime();

			} else if (getTime() > lastTime + frameTime) {
				long delta = getTime() - lastTime;
				int frameCount = (int) (delta / frameTime);
				pos += frameCount;
				if (!loop) {
					if (pos > frames.length - 1) {
						pos = frames.length - 1;
					}
				} else {
					pos = pos % frames.length;
				}
				lastTime = getTime();
			}
		}

		public void set(int frame) {
			pos = frame;
		}

		public int get() {
			return frames[pos];
		}
	}
}
