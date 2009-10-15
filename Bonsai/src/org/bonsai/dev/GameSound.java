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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class GameSound extends GameComponent {
	private HashMap<String, String> soundTypes = new HashMap<String, String>();
	private HashMap<String, byte[]> sounds = new HashMap<String, byte[]>();
	private HashMap<String, SoundObject> soundsStatus = new HashMap<String, SoundObject>();

	public GameSound(Game g) {
		super(g);
	}

	public boolean load(String id, String filename) {
		String type = "";
		if (filename.toLowerCase().endsWith("wav")) {
			type = "WAV";
		} else if (filename.toLowerCase().endsWith("ogg")) {
			type = "OGG";
		} else {
			return false;
		}
		try {
			InputStream stream = Game.class.getResourceAsStream(filename);
			int size = stream.available();
			byte[] bytes = new byte[size];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = stream.read(bytes, offset, bytes.length
							- offset)) >= 0) {
				offset += numRead;
			}
			sounds.put(id, bytes);
			soundTypes.put(id, type);
			stream.close();
			return true;

		} catch (IOException e) {
			return false;
		}
	}

	public boolean isPlaying(String id) {
		if (soundsStatus.containsKey(id)) {
			return soundsStatus.get(id).status == 1;
		} else {
			return false;
		}
	}

	public boolean isPaused(String id) {
		if (soundsStatus.containsKey(id)) {
			return soundsStatus.get(id).status == 3;
		} else {
			return false;
		}
	}

	public void stop(String id) {
		if (soundsStatus.containsKey(id)) {
			soundsStatus.get(id).status = 2;
		}
	}

	public void pause(String id, boolean pause) {
		if (soundsStatus.containsKey(id)) {
			if (soundsStatus.get(id).status != 2) {
				soundsStatus.get(id).status = pause ? 3 : 1;
			}
		}
	}

	public void setVolume(String id, float volume) {
		if (soundsStatus.containsKey(id)) {
			soundsStatus.get(id).volume = volume;
			soundsStatus.get(id).toVolume = volume;
		}
	}

	public void setFadeVolume(String id, float volume) {
		if (soundsStatus.containsKey(id)) {
			try {
				soundsStatus.get(id).toVolume = volume;
				soundsStatus.get(id).volumeChanged = true;
				soundsStatus.get(id).silent = false;
			} catch (Exception e) {
				setFadeVolume(id, volume);
			}
		}
	}

	public boolean isSilent(String id) {
		if (soundsStatus.containsKey(id)) {
			return soundsStatus.get(id).volume == 0.0f;
		} else {
			return true;
		}
	}

	public void play(String id, boolean stop) {
		play(id, stop, false, 1.0f);
	}

	public void play(String id, boolean stop, boolean loop, float volume) {
		if (game.hasSound() && sounds.containsKey(id)) {
			if (isPlaying(id) && stop) {
				soundsStatus.get(id).status = 2;
			}
			soundsStatus.put(id, null);
			SoundObject snd = soundTypes.get(id) == "WAV" ? new SoundObject()
					: new SoundObjectOgg();
			snd.loop = loop;
			snd.volume = volume;
			snd.toVolume = volume;
			snd.initSound(sounds.get(id));
			snd.start();
			soundsStatus.put(id, snd);
		}
	}

	public boolean delete(String id) {
		if (soundsStatus.containsKey(id)) {
			sounds.remove(id);
			soundTypes.remove(id);
			soundsStatus.remove(id);
			return true;
		} else {
			return false;
		}
	}

	public void stopAll() {
		for (String id : sounds.keySet()) {
			stop(id);
		}
	}

	public void pauseAll(boolean mode) {
		for (String id : sounds.keySet()) {
			pause(id, mode);
		}
	}

	public boolean init() {
		AudioFormat[] formats = new AudioFormat[] {
				new AudioFormat(44100.0f, 16, 2, true, false),
				new AudioFormat(22050.0f, 16, 2, true, false),
				new AudioFormat(11050.0f, 16, 2, true, false) };

		for (AudioFormat format : formats) {
			try {
				DataLine.Info info = new DataLine.Info(SourceDataLine.class,
						format);
				SourceDataLine line = (SourceDataLine) AudioSystem
						.getLine(info);
				line.open(format);
				line.start();
				line.close();
				return true;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
