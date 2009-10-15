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

import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class GameImage extends GameComponent {
	public GameImage(Game g) {
		super(g);
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
		return create(width, height, true);
	}

	public BufferedImage create(int width, int height, boolean alpha) {
		BufferedImage buffer = game.config.createCompatibleImage(width, height,
				alpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
		return buffer;
	}

	public BufferedImage get(String file) {
		URL filename = getURL(file);
		if (filename == null) {
			return null;
		} else {
			try {
				return ImageIO.read(filename);
			} catch (IOException e) {
			}
			return null;
		}
	}

	public BufferedImage[] gets(String filename, int cols, int rows) {
		BufferedImage image = get(filename);
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