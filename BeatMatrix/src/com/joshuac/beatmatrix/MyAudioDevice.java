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

import android.app.Activity;
import android.content.Context;
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
	private byte[] buffer;
//	private byte[] bufferCopy = new byte[bufferSize];
	private boolean playing = false;
	private boolean looping = false;
	private OnCompletionListener onCompletionListener;
	private EqualizingFilter eq;
	
	private double volume = 1; //volume as a multiplicative factor
	private double playbackSpeed = 1; //play speed as a multiplicative factor?
	private double bass = 0; //bass in dB
	private double treble = 0; //treble in dB
	private int startPosition = 0; //offset of first sample to play, in bytes (SHOULD BE EVEN)
	private int endPosition; //equals 1 + offset of last sample to play, in bytes (SHOULD BE EVEN)
	private int currentPosition; // offset of current sample, in bytes
	private double trackLength; //end time of file in seconds, stored so we know when to set endPosition to exact end
	
	private boolean quittingTime = false; //time to pack it up and go home
	private boolean usingRawResource = false; //created from resource in raw
	private Context context;
	private int resourceName;
	private int defaultPriority;
	
//	private boolean editTreble = false;
//	private boolean editVolume = false;
//	byte buffn1 = 0; //buffer[-1]	
//	byte buffn2 = 0; //buffer[-2]
	
	public static abstract class OnCompletionListener {
		public OnCompletionListener(){}
		
		public Activity myActivity = null;

		public abstract void onCompletion();
	}

	public MyAudioDevice(File openFile)
	{
		defaultPriority = Thread.currentThread().getPriority();
		file = openFile;
		
		reader = new WavReader();
		
		playbackSpeed = 1;
		volume = 1;
		startPosition = 0;

		startStream();
		endPosition = info.dataSize;
		trackLength = endPosition/(2.0*info.rate*info.channels);
		
		createAT();
		
		eq = new EqualizingFilter(info.rate, info.channels);
	}

	public MyAudioDevice(Context c, int resid) {
		defaultPriority = Thread.currentThread().getPriority();
		usingRawResource = true;
		resourceName = resid;
		context = c;
		reader = new WavReader();
		
		playbackSpeed = 1;
		volume = 1;
		startPosition = 0;

		startStream();
		endPosition = info.dataSize;
		trackLength = endPosition/(2.0*info.rate*info.channels);
		
		createAT();
		
		eq = new EqualizingFilter(info.rate, info.channels);
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
		
		while(!quittingTime) {
			if (playing) {
				Thread.currentThread().setPriority(defaultPriority);
				if (track.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
					track.play();
				}
				//Write a buffer of data to the track
				try {
					if (dis.available() > 0 && bufferSize < (endPosition - currentPosition)) {
						buffer = reader.readToPcm(info, dis, bufferSize);
						//preprocessBuffer();
						buffer = eq.filter(buffer);
						track.write( buffer, 0, buffer.length );
						currentPosition += buffer.length;
					}
					else if (looping) {
						//write any remaining bits to the track
						if (endPosition - currentPosition > 0){
							buffer = reader.readToPcm(info, dis, endPosition - currentPosition);
							//preprocessBuffer();
							buffer = eq.filter(buffer);
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
						if (onCompletionListener != null) {
							onCompletionListener.myActivity.runOnUiThread(new Runnable() {
								public void run() {
									onCompletionListener.onCompletion();
								}
							});
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				if (track.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
					track.pause();
				}
				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
				Thread.yield();
			}
		}
		
		release();
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
			if (usingRawResource){
				is = context.getResources().openRawResource(resourceName);
			}
			else {
				is = new FileInputStream(file);
			}
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
	
/*	public void reset(File nFile) {
		playing = false;
		looping = false;
		file = nFile;
		restartStream();
		createAT();
	}*/

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
		//volume is a scalar between 0.0 and 1.0. Default is 1.0
		this.volume = volume;
		track.setStereoVolume((float)volume, (float)volume);
	}

	public double getPlaybackSpeed() {
		return playbackSpeed;
	}

	public void setPlaybackSpeed(double playbackSpeed) {
		this.playbackSpeed = playbackSpeed;
		track.setPlaybackRate((int) Math.round(info.rate*playbackSpeed));
	}
	
	
	//Start Time / End Time set/get methods
	
	public double getStartTime() {
		return startPosition/(2.0*info.rate*info.channels);
	}
	
	public int setStartTime(double startTime) {
		if (startTime > 0 && startTime <= trackLength) {
			startPosition = 2*info.channels*(int) Math.round(startTime*info.rate);
		}
		else if (startTime == 0.0) {
			startPosition = 0;
		}
		else {
			return -1;
		}
		return 0;
			
			//throw new IllegalArgumentException();
	}
	
	public double getEndTime() {
		return endPosition/(2.0*info.rate*info.channels);
	}
	
	public int setEndTime(double endTime) {
		if (endTime >= 0 && endTime < trackLength) {
			endPosition = 2*info.channels*(int) Math.round(endTime*info.rate);
		}
		else if (endTime == trackLength) {
			endPosition = info.dataSize;
		}
		else {
			return -1;
		}
		return 0;
			
			
			//throw new IllegalArgumentException();
	}
	
	public double getTrackLength() {
		//length of the original file
		return trackLength;
	}
	
	public double getCurrentLength() {
		//length of the file from start time to end time at default speed
		return endPosition/(2.0*info.rate*info.channels)-startPosition/(2.0*info.rate*info.channels);
	}
	
	public double getCurrentTime() {
		//current track position, at default speed
		return currentPosition/(2.0*info.rate*info.channels);
	}
	
	
	//Equalizer methods
	//NOTE: bass and treble gains should be in dB. Absolute desired gain = 10^(gain/20)
	public void setBass (double new_bass) {
		bass = new_bass;
		eq.setBass(bass);
	}
	
	public void setTreble (double new_treble) {
		treble = new_treble;
		eq.setTreble(treble);
	}
	
	public double getBass() {
		return bass;
	}
	
	public double getTreble() {
		return treble;
	}

	public void quit() {
		quittingTime = true;
	}

	public boolean usingRes() {
		return usingRawResource;
	}

	public File getFile() {
		return file.getAbsoluteFile();
	}

	public int getResid() {
		return resourceName;
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

	/*private void preprocessBuffer() 
	{
		if (editVolume|| editTreble) //add effects below
		{
			//copy buffer before editing
			System.arraycopy(buffer,0,bufferCopy,0,bufferSize);
			
			for (int i = 0; i < buffer.length; i+=2) 
			{
				//need to distinguish between two and one stream still
				
				//Modify buffer for volume
				//Note: volume increase results in poor sound quality
				if(editVolume)
				{
					short newValue = (short) ((buffer[i+1]<<8)|(buffer[i]));
					newValue = (short) Math.round(newValue*volume);
					buffer[i+1] = (byte) (newValue>>8);
					buffer[i] = (byte) newValue;
				}//end edit volume
				
				//modify treble
				//need to test with two streams still..
				if(editTreble)
				{
					if(info.channels == 1)	//mono
					{ 
						if(i==0)
						{
							buffer[i]	=	(byte) (bufferCopy[i]	- buffn1);
							buffer[i+1]	=	(byte) (bufferCopy[i+1]	- bufferCopy[i]);
						}
						else
						{
							buffer[i]	=	(byte) (bufferCopy[i]	- bufferCopy[i-1]);
							buffer[i+1]	=	(byte) (bufferCopy[i+1]	- bufferCopy[i]);
						}
						//check for greater than byte or less than zero
						//0 to 255 or -32768 to 32767 ?
					}
					else 					//stereo
					{
						if(i==0)
						{
							buffer[i]	=	(byte) (bufferCopy[i]	- buffn2);
							buffer[i+1]	=	(byte) (bufferCopy[i+1]	- buffn1);
						}
						else
						{
							buffer[i]	=	(byte) (bufferCopy[i]	- bufferCopy[i-2]);
							buffer[i+1]	=	(byte) (bufferCopy[i+1]	- bufferCopy[i-1]);
						}
						//check for greater than byte or less than zero
						//0 to 255 or -32768 to 32767 ?
					}
				}//end edit treble
				
				//store buffer[-1] and buffer[-2]
				if(i==bufferSize-1)
				{
					buffn1 = buffer[i];
					buffn2 = buffer[i-1];
				}
			}//end for loop
		}//end adding effects

	}//end preprocessBuffer*/
}