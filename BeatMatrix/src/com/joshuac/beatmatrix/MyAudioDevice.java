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
	
	private double volume = 1; //volume as a multiplicative factor
	//TODO: Make tempo change the actual tempo
	private double tempo = 1; //play speed as a multiplicative factor?
	private int startPosition = 0; //offset of first sample to play, in bytes (SHOULD BE EVEN)
	private int endPosition; //equals 1 + offset of last sample to play, in bytes (SHOULD BE EVEN)
	private int currentPosition; // offset of current sample, in bytes
	private double trackLength; //end time of file in seconds, stored so we know when to set endPosition to exact end
	
	
	public interface OnCompletionListener {
		public void onCompletion();
	}

	public MyAudioDevice(File openFile)
	{
		file = openFile;
		reader = new WavReader();
		
		tempo = 1;
		volume = 1;
		startPosition = 0;

		startStream();
		endPosition = info.dataSize;
		trackLength = endPosition/(2.0*info.rate*info.channels);
		
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
					if (dis.available() > 0 && bufferSize < (endPosition - currentPosition)) {
						buffer = reader.readToPcm(info, dis, bufferSize);
						preprocessBuffer();
						track.write( buffer, 0, buffer.length );
						currentPosition += buffer.length;
					}
					else if (looping) {
						//write any remaining bits to the track
						if (endPosition - currentPosition > 0){
							buffer = reader.readToPcm(info, dis, endPosition - currentPosition);
							preprocessBuffer();
							track.write( buffer, 0, buffer.length );
						}
						
						//restart the input stream if looping
						restart();
						/*buffer = reader.readToPcm(info, dis, bufferSize);
						track.write( buffer, 0, buffer.length );
						currentPosition += bufferSize;*/
					}
					else {
						playing = false;
						//TODO: Figure this mofo out.
						//onCompletionListener.onCompletion();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void preprocessBuffer() {
		//Modify buffer for volume and tempo. TODO: Change tempo in preprocessing
		//Note: volume increase results in poor sound quality
		if (volume != 1) {
			for (int i = 0; i<buffer.length; i+=2) {
				short newValue = (short) ((buffer[i+1]<<8)|(buffer[i]));
				newValue = (short) Math.round(newValue*volume);
				buffer[i+1] = (byte) (newValue>>8);
				buffer[i] = (byte) newValue;
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
			dis.skip(startPosition);
			currentPosition = startPosition;
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
			track.release();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Avoid touching these. For debug purposes only...?
	public int getStartPosition() {
		return startPosition;
	}
	
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	public int getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}

	public int getCurrentPosition() {
		return currentPosition;
	}
	//End debug methods

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public double getTempo() {
		return tempo;
	}

	public void setTempo(double tempo) {
		this.tempo = tempo;
	}
	
	
	//Start Time / End Time set/get methods
	
	public double getStartTime() {
		return startPosition/(2.0*info.rate*info.channels);
	}
	
	public void setStartTime(double startTime) {
		if (startTime > 0 && startTime <= trackLength) {
			startPosition = (int) Math.round(2*startTime*info.rate*info.channels);
		}
		else if (startTime == 0.0) {
			startPosition = 0;
		}
		else throw new IllegalArgumentException();
	}
	
	public double getEndTime() {
		return endPosition/(2.0*info.rate*info.channels);
	}
	
	public void setEndTime(double endTime) {
		if (endTime >= 0 && endTime < trackLength) {
			endPosition = (int) Math.round(2*endTime*info.rate*info.channels);
		}
		else if (endTime == trackLength) {
			endPosition = info.dataSize;
		}
		else throw new IllegalArgumentException();
	}
	
	public double getTrackLength() {
		return trackLength;
	}
	
	public double getCurrentLength() {
		return endPosition/(2.0*info.rate*info.channels)-startPosition/(2.0*info.rate*info.channels);
	}
	
	public double getCurrentTime() {
		return endPosition/(2.0*info.rate*info.channels);
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