package com.joshuac.beatmatrix;

//import android.media.AudioManager;
import android.content.Context;
import java.io.File;

import com.joshuac.beatmatrix.MyAudioDevice.OnCompletionListener;

public class MyAudioDeviceThread extends Thread implements Runnable
{

	//private volatile boolean playing;
	MyAudioDevice myAudio;
	Context context;			//context of the application
	File track; 		//file descriptor of the track
	
	/*
	 * Constructor(s)
	 */
	
	public MyAudioDeviceThread(Context c, File f)
	{
	    super();
	    this.context = c;
	    this.track = f;
	    
	    //Start Audio Device here
		myAudio = new MyAudioDevice(track);
	}
	
	public MyAudioDeviceThread(Context c, File f, OnCompletionListener completionListener) {
		this(c,f);
		setOnCompletionListener(completionListener);
	}
	
	/*
	 * Methods
	 */
	
	
	//@see java.lang.Thread#run()
	//starts the thread
	public void run()
	{
		myAudio.start();
	}//end run
	

	//play/restarts the track
	public void play()
	{
		myAudio.setLooping(false);
		myAudio.restart();
		myAudio.play();
	}
	
	//loops the track until tapped
	public void loop() {
		myAudio.setLooping(true);
		myAudio.restart();
		myAudio.play();
	}
	
	//pause the current track
	public void pause()
	{
		myAudio.setLooping(false);
		myAudio.restart();
		myAudio.pause();
	}
	
	/*
	 * Getters / Setters
	 */
	
	public void setTrack(File f)
	{
		this.track = f;
		if (myAudio != null) {
            myAudio.release();
        }
		myAudio = new MyAudioDevice(track);
	}

	public void setOnCompletionListener(OnCompletionListener completionListener) {
		// sets the completion listener for this media player
		myAudio.setOnCompletionListener(completionListener);
	}
	
	public void setStartTime(double start_time) {
		myAudio.setStartTime(start_time);
	}
	
	public void setEndTime(double end_time) {
		myAudio.setEndTime(end_time);
	}
	
	public void setVolume(float volume) {
		myAudio.setVolume(volume);
	}
	
	public void setPlaybackSpeed(double speed) {
		myAudio.setPlaybackSpeed(speed);
	}
	
	public double getTrackLength() {
		return myAudio.getTrackLength();
	}
	
/*	public MediaPlayer getMP() {
		return mp;
	}*/
}