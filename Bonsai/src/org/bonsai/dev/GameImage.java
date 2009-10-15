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

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.net.URL;

public class GameImage {
	private Game game;
	private MediaTracker tracker;

	public static enum Type {
		OPAQUE, BITMASK, TRANSLUCENT
	}

	public GameImage(Game g) {
		game = g;
		Canvas canvas = new Canvas(game.config);
		tracker = new MediaTracker(canvas);
	}

	private static int getAlpha(Type type) {
		if (type == Type.OPAQUE) {
			return Transparency.OPAQUE;
		} else if (type == Type.TRANSLUCENT) {
			return Transparency.TRANSLUCENT;
		} else {
			return Transparency.BITMASK;
		}
	}

	public BufferedImage getScreen() {
		int w = game.width();
		int h = game.height();
		int s = game.scale();
		BufferedImage buffer = create(w * s, h * s);
		Graphics2D g = (Graphics2D) buffer.getGraphics();
		if (s != 1) {
			g.drawImage(game.background, 0, 0, w * s, h * s, 0, 0, w, h, null);
		} else {
			g.drawImage(game.background, 0, 0, null);
		}
		g.dispose();
		return buffer;
	}

	private URL getURL(String filename) {
		URL url = Game.class.getResource(filename);
		return url;
	}

	public BufferedImage create(int width, int height) {
		return create(width, height, Type.TRANSLUCENT);
	}

	public BufferedImage create(int width, int height, Type type) {
		BufferedImage buffer = game.config.createCompatibleImage(width, height,
				getAlpha(type));
		return buffer;
	}

	public BufferedImage get(String file) {
		return get(file, Type.TRANSLUCENT);
	}

	public BufferedImage get(String file, Type type) {
		URL filename = getURL(file);
		if (filename == null) {
			return null;
		}
		java.awt.Image image = Toolkit.getDefaultToolkit().getImage(filename);
		tracker.addImage(image, 0);
		try {
			tracker.waitForAll();

		} catch (InterruptedException e) {
			tracker.removeImage(image, 0);
		} catch (NullPointerException e) {

		}
		BufferedImage buffer = game.config.createCompatibleImage(image
				.getWidth(null), image.getHeight(null), getAlpha(type));

		Graphics2D g = buffer.createGraphics();
		g.setComposite(AlphaComposite.Src);
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return buffer;

	}

	public BufferedImage[] gets(String filename, int cols, int rows) {
		return gets(filename, cols, rows, Type.TRANSLUCENT);
	}

	public BufferedImage[] gets(String filename, int cols, int rows, Type type) {
		BufferedImage image = get(filename, type);
		if (image == null) {
			return null;
		}
		BufferedImage[] buffer = new BufferedImage[cols * rows];
		int width = image.getWidth() / cols;
		int height = image.getHeight() / rows;
		int i = 0;
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				buffer[i] = game.config.createCompatibleImage(width, height,
						image.getColorModel().getTransparency());
				Graphics2D g = buffer[i].createGraphics();
				g.drawImage(image, 0, 0, width, height, x * width, y * height,
						(x + 1) * width, (y + 1) * height, null);
				g.dispose();
				i++;
			}
		}
		return buffer;
	}

	public BufferedImage flip(BufferedImage image, boolean h, boolean v) {
		BufferedImage buffer = game.config.createCompatibleImage(image
				.getWidth(), image.getHeight(), image.getColorModel()
				.getTransparency());

		Graphics2D g = buffer.createGraphics();
		g.drawImage(image, h ? image.getWidth() : 0, v ? image.getHeight() : 0,
				h ? 0 : image.getWidth(), v ? 0 : image.getHeight(), 0, 0,
				image.getWidth(), image.getHeight(), null);

		g.dispose();
		return buffer;
	}

	public BufferedImage[] flips(BufferedImage[] images, boolean h, boolean v) {
		BufferedImage[] buffer = new BufferedImage[images.length];
		for (int i = 0; i < images.length; i++) {
			buffer[i] = flip(images[i], h, v);
		}
		return buffer;
	}
}