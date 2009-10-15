/**
 *	Version 1.00
 *	Copyright (C) 2009 Ivo Wetzel
 *	<http://code.google.com/p/bonsaigamelibrary/>
 *
 *
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

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.bonsai.ext.Base64;

import netscape.javascript.JSObject;

public class Game extends Applet {
	// Applet
	private static final long serialVersionUID = -7860545086276629929L;

	// Graphics
	protected GraphicsConfiguration config = GraphicsEnvironment
			.getLocalGraphicsEnvironment().getDefaultScreenDevice()
			.getDefaultConfiguration();

	protected Canvas canvas;
	private BufferStrategy strategy;
	protected BufferedImage background;
	private Graphics2D graphics;
	private int width;
	private int height;
	protected int scale = 1;

	// Game Stuff
	private boolean gameLoaded = false;
	protected boolean gameSound = false;
	private boolean isRunning = true;
	protected boolean paused = false;
	protected boolean focused = false;
	protected boolean pausedOnFocus = false;

	private int maxFPS = 30;
	private int currentFPS = 0;
	private long fpsWait = (long) (1.0 / maxFPS * 1000);
	private long gameTime = 0;

	private Thread gameLoader = null;

	// GUI
	protected JFrame frame = null;
	private Applet applet = null;
	private boolean hasMenu = false;

	// Classes
	protected GameAnimation animation = new GameAnimation(this);
	protected GameSound sound = new GameSound(this);
	protected GameImage image = new GameImage(this);
	protected GameInput input = new GameInput(this);
	protected GameFont font = new GameFont(this);
	protected GameTimer timer = new GameTimer(this);
	protected GameMenu menu = null;

	// Getters
	public JFrame getFrame() {
		return frame;
	}

	public Applet getApplet() {
		return applet;
	}

	public boolean isApplet() {
		return applet != null;
	}

	public boolean hasMenu() {
		return menu != null;
	}

	public boolean isJar() {
		if (!isApplet()) {
			try {
				return this.getClass().getProtectionDomain().getCodeSource()
						.getLocation().toURI().getPath().toLowerCase()
						.endsWith(".jar");
			} catch (URISyntaxException e) {
				return false;
			}
		} else {
			return true;
		}
	}

	public String getPath() {
		if (!isApplet()) {
			try {

				String path = this.getClass().getProtectionDomain()
						.getCodeSource().getLocation().toURI().getPath();

				if (path.toLowerCase().endsWith(".jar")) {
					path = path.substring(0, path.lastIndexOf("/") + 1);
				}
				return path;
			} catch (URISyntaxException e) {
				return null;
			}
		} else {
			return "";
		}
	}

	public String getBasePath() {
		return getPath();
	}

	/*
	 * GUI ---------------------------------------------------------------------
	 */
	public void Frame(String title, int sizex, int sizey, boolean scaled, boolean menu) {
		scale = scaled ? 2 : 1;
		width = sizex;
		height = sizey;

		// Create frame
		frame = new JFrame(config);
		frame.setLayout(new BorderLayout(0, 0));
		frame.setResizable(false);
		frame.setTitle(title);
		frame.addWindowListener(new FrameClose());
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setVisible(true);
		resize();
		
		// Engine
		initEngine(frame);
		hasMenu = menu;
		initThreads();
		canvas.requestFocus();
	}

	private void resize() {
		frame.setSize((width * scale) + frame.getInsets().left
				+ frame.getInsets().right, (height * scale)
				+ frame.getInsets().top + frame.getInsets().bottom
				+ (menu != null ? menu.menuBar.getSize().height : 23));
	}

	public static void main(String args[]) {
		new Game().Frame("Bonsai Game Library v1.00", 320, 240, false, true);
	}

	public void onMenu(String menu) {
	}

	private class FrameClose extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			isRunning = false;
		}
	}

	/*
	 * Applet
	 * ---------------------------------------------------------------------
	 */	
	@Override
	public void start() {
		if (strategy == null) {
			isRunning = true;
			scale = getParameter("scaled") != null ? 2 : 1;
			width = getWidth() / scale;
			height = getHeight() / scale;
			setLayout(null);
			initEngine(this);
			applet = this;
			initThreads();
			canvas.requestFocus();
		}
	}

	public void destroy() {
		exitGame();
	}

	public int height() {
		return height;
	}

	public int width() {
		return width;
	}

	public int scale() {
		return scale;
	}

	/*
	 * Gameloader --------------------------------------------------------------
	 */
	private void initEngine(Container parent) {
		// Canvas
		canvas = new Canvas(config);
		canvas.setSize(width * scale, height * scale);
		parent.add(canvas, 0);

		// Add input listeners
		canvas.addMouseListener(input);
		canvas.addMouseMotionListener(input);
		canvas.addKeyListener(input);
		canvas.addFocusListener(input);
		canvas.setIgnoreRepaint(true);

		// Create the buffer strategy
		background = image.create(width, height, false);
		canvas.createBufferStrategy(2);
		do {
			strategy = canvas.getBufferStrategy();
		} while (strategy == null);

	}

	private void initThreads() {
		new GameLoop().start();
		gameLoader = new GameLoader(this);
		gameLoader.start();
	}

	private class GameLoader extends Thread {
		private Game game;
		public GameLoader(Game g) {
			game = g;
			setDaemon(true);
			setName("Bonsai-GameLoader");
		}

		@Override
		public void run() {
			menu = new GameMenu(game, hasMenu);
			if (!isApplet()) {
				game.frame.validate();
				game.resize();
			}
			
			// Init Loading
			initGame();
			gameSound = sound.init(); // This actually takes time!
			game.canvas.requestFocus();
			gameLoaded = true;
			finishLoading();

			// Fix some of the graphical lag
			// This hack lowers the systems interrupt rate so that Thread.sleep
			// becomes more precise
			try {
				Thread.sleep(Integer.MAX_VALUE);

			} catch (InterruptedException e) {
				isRunning = false;
				Thread.interrupted();
			}
		}
	}

	public void initGame() {
	}

	public void initLoading() {
	}

	public void renderLoading(Graphics2D g) {
	}

	public void finishLoading() {
		menu.enable(true);
	}

	/*
	 * Gameloop ----------------------------------------------------------------
	 */
	private class GameLoop extends Thread {
		@Override
		public void run() {
			setName("Bonsai-GameLoop");
			initLoading();
			int fpsCount = 0;
			long fpsTime = 0;

			Graphics2D g = (Graphics2D) background.getGraphics();
			main: while (true) {
				// Pausing
				long renderStart = System.nanoTime();
				if (input.keyPressed(java.awt.event.KeyEvent.VK_P)) {
					pause(!paused);
				}

				// Update Game
				if (!paused && gameLoaded) {
					updateGame();
					for (GameAnimation.Animation anim : animation.animationList) {
						anim.update();
					}
				}
				input.clearKeys();
				input.clearMouse();

				// Render
				do {
					Graphics2D bg = getBuffer();
					if (!isRunning) {
						break main;
					}
					if (!gameLoaded) {
						renderLoading(g);
					} else {
						renderGame(g);
					}
					if (scale != 1) {
						bg.drawImage(background, 0, 0, width * scale, height
								* scale, 0, 0, width, height, null);
					} else {
						bg.drawImage(background, 0, 0, null);
					}
				} while (updateScreen() == false);

				// Limit FPS
				try {
					if (!paused) {
						// Use Nanoseconds instead of currentTimeMillis which
						// has a much lower resolution(based on the OS interrupt
						// rate) and would result in too high FPS.

						// Note: There is a way to set the interrupt rate lower
						// which is done by many programs, mostly media players.
						// That means if you use currentTimeMillis and play a
						// track, your FPS is okay, but without the music it's
						// too high.

						// More on this:
						// <http://blogs.sun.com/dholmes/entry/inside_the_hotspot_vm_clocks>
						long renderTime = (System.nanoTime() - renderStart) / 1000000;
						Thread.sleep(Math.max(0, fpsWait - renderTime));
						renderTime = (System.nanoTime() - renderStart) / 1000000;
						if (gameLoaded) {
							gameTime += renderTime;
						}
						fpsTime += renderTime;
						fpsCount += 1;
						if (fpsTime > 1000 - fpsWait) {
							currentFPS = fpsCount;
							fpsCount = 0;
							fpsTime = 0;
						}
					} else {
						Thread.sleep(100);
					}
				} catch (InterruptedException e) {
					Thread.interrupted();
					break;
				}
			}

			// Clean up
			gameLoader.interrupt();
			finishGame();
			sound.stopAll();
			if (!isApplet()) {
				frame.dispose();
			} else {
				applet = null;
			}
		}
	}

	private Graphics2D getBuffer() {
		if (graphics == null) {
			try {
				graphics = (Graphics2D) strategy.getDrawGraphics();
			} catch (IllegalStateException e) {
				return null;
			}
		}
		return graphics;
	}

	private boolean updateScreen() {
		graphics.dispose();
		graphics = null;
		try {
			strategy.show();
			Toolkit.getDefaultToolkit().sync();
			return (!strategy.contentsLost());

		} catch (NullPointerException e) {
			return true;

		} catch (IllegalStateException e) {
			return true;
		}
	}

	/*
	 * Game methods ------------------------------------------------------------
	 */
	public void renderGame(Graphics2D g) {
	}

	public void updateGame() {
	}

	public void finishGame() {
	}

	public void exitGame() {
		isRunning = false;
	}

	// Setters & Getters
	public boolean isRunning() {
		return isRunning;
	}

	public boolean hasSound() {
		return gameSound;
	}

	public long getTime() {
		return gameTime;
	}

	public void setFPS(int fps) {
		maxFPS = fps;
		fpsWait = (long) (1.0 / maxFPS * 1000);
	}

	public int getFPS() {
		return currentFPS;
	}

	public void pause(boolean mode) {
		paused = mode;
		menu.getItem("pause").setSelected(paused);
		sound.pauseAll(paused);
	}

	public boolean isPaused() {
		return paused;
	}

	public void pauseOnFocus(boolean mode) {
		pausedOnFocus = mode;
	}

	public boolean isFocused() {
		return focused;
	}

	/*
	 * Saving ------------------------------------------------------------------
	 */
	public boolean saveGame(String filename, String cookiename) {
		try {
			if (!isApplet()) {
				OutputStream stream = new FileOutputStream(new File(filename));
				writeSave(stream);
				stream.close();

			} else {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				writeSave(stream);
				JSObject win = JSObject.getWindow(this);
				JSObject doc = (JSObject) win.getMember("document");
				String data = cookiename + "save="
						+ Base64.encodeBytes(stream.toByteArray())
						+ "; path=/; expires=Thu, 31-Dec-2019 12:00:00 GMT";

				doc.setMember("cookie", data);
				stream.close();
			}

		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public void writeSave(OutputStream stream) throws IOException {
	}

	/*
	 * Loading -----------------------------------------------------------------
	 */

	public boolean loadGame(String filename, String cookiename) {
		try {
			InputStream stream = null;
			if (!isApplet()) {
				stream = new FileInputStream(filename);

			} else {
				String data = null;
				JSObject myBrowser = (JSObject) JSObject.getWindow(this);
				JSObject myDocument = (JSObject) myBrowser
						.getMember("document");

				String myCookie = (String) myDocument.getMember("cookie");
				if (myCookie.length() > 0) {
					String get = cookiename + "save=";
					int offset = myCookie.indexOf(get);
					if (offset != -1) {
						offset += get.length();
						int end = myCookie.indexOf(";", offset);
						if (end == -1)
							end = myCookie.length();
						data = myCookie.substring(offset, end);
					}
				}

				// Decode
				if (data != null) {
					byte[] buffer = Base64.decode(data);
					stream = new ByteArrayInputStream(buffer);
				}
			}

			// No Stream
			if (stream == null) {
				return false;
			}

			// Empty Stream
			if (stream.available() <= 0) {
				stream.close();
				return false;
			}

			// Read Save
			readSave(stream);
			stream.close();

		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public void readSave(InputStream stream) throws IOException {
	}
}
