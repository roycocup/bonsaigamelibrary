package org.bonsai.dev;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

public abstract class SoundObject extends Thread {
	public float volume = 1.0f;
	public float toVolume = 0.0f;
	public boolean volumeChanged = false;
	public int status = 1;
	public boolean loop = false;
	protected byte[] byteData = null;
	public SourceDataLine line = null;
	public AudioInputStream audioInputStream = null;

	public abstract void initSound(final byte[] bytes);

	public abstract void startSound();

	@Override
	public final void run() {
		startSound();
	}
}
