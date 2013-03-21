package com.joshuac.beatmatrix;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.joshuac.beatmatrix.AudioReader.FileInfo;
import com.joshuac.beatmatrix.WavReader.WavException;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class MyAudioDevice
{
	private File file;
	private DataInputStream dis;
	private AudioTrack track;
	private int bufferSize;
	private AudioReader reader;
	private FileInfo info;
	private byte[] buffer = new byte[bufferSize];
	private boolean playing = false;
	private boolean looping = false;
	private OnCompletionListener onCompletionListener;
	
	//TODO make setTempo, setStartTime, setEndTime
	
	public interface OnCompletionListener {
		public void onCompletion();
	}

	public MyAudioDevice(File openFile)
	{
		file = openFile;
		reader = new WavReader();

		startStream();
		createAT();
	}

	private void createAT() {
		bufferSize = android.media.AudioTrack.getMinBufferSize(info.rate,
			(info.channels==1)? AudioFormat.CHANNEL_OUT_MONO:AudioFormat.CHANNEL_OUT_STEREO,
			AudioFormat.ENCODING_PCM_16BIT);
		track = new AudioTrack(AudioManager.STREAM_MUSIC, info.rate,
			(info.channels==1)? AudioFormat.CHANNEL_OUT_MONO:AudioFormat.CHANNEL_OUT_STEREO,
			AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
	}

	public void start() {
		track.play();
		
		while(true) {
			if (playing) {
				//Write a buffer of data to the track
				try {
					if (dis.available() > 0) {
						buffer = reader.readToPcm(info, dis, bufferSize);
						track.write( buffer, 0, buffer.length );
					}
					else if (looping) {
						//restart the input stream if looping
						restart();
						buffer = reader.readToPcm(info, dis, bufferSize);
						track.write( buffer, 0, buffer.length );
					}
					else {
						playing = false;
						//onCompletionListener.onCompletion();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void restartStream() {
		try {
			dis.close();
			startStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startStream() {
		
		InputStream is;
		try {
			is = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			dis = new DataInputStream(bis);
			info = reader.readHeader(dis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WavException e) {
			e.printStackTrace();
		}
	}
	
	public void play(){
		playing = true;
	}
	
	public void pause() {
		playing = false;
	}
	
	public void restart() {
		restartStream();
	}
	
	public void setLooping(boolean loop) {
		looping = loop;
	}
	
	public void reset(File nFile) {
		playing = false;
		looping = false;
		file = nFile;
		restartStream();
		createAT();
	}

	public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
		this.onCompletionListener = onCompletionListener;
	}

	public boolean isPlaying() {
		return playing;
	}

	public void release() {
		try {
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

/*	public void writeSamples(byte[] samples) 
	{	
		fillBuffer( samples );
		track.write( buffer, 0, samples.length );
	}
 
	private void fillBuffer( byte[] samples )
	{
		if( buffer.length < samples.length )
			buffer = new byte[samples.length];

		for( int i = 0; i < samples.length; i++ )
			buffer[i] = samples[i];
	}
*/
}