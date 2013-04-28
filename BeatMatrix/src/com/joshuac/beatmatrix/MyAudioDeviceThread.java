package com.joshuac.beatmatrix;

//import android.media.AudioManager;
import android.content.Context;
import java.io.File;

import com.joshuac.beatmatrix.ChooseFileDialog.FileOrRes;
import com.joshuac.beatmatrix.MyAudioDevice.OnCompletionListener;

public class MyAudioDeviceThread extends Thread implements Runnable
{

	//private volatile boolean playing;
	MyAudioDevice myAudio;
	Context context;			//context of the application
	File track = null; 		//file descriptor of the track
	int resid;
	
	/*
	 * Constructor(s)
	 */
	
	public MyAudioDeviceThread(Context c, int resource)
	{
	    super();
	    context = c;
	    resid = resource;
	    System.out.println(this.toString() + ": in MADT(c,r) ");
	    
	    //Start Audio Device here
		myAudio = new MyAudioDevice(c,resource);
	}
	
	public MyAudioDeviceThread(Context c, File f)
	{
	    super();
	    this.context = c;
	    this.track = f;
	    System.out.println(this.toString() + ": in MADT(c,f) " +  track.getAbsolutePath());
	    
	    //Start Audio Device here
		myAudio = new MyAudioDevice(track);
	}
	
	public MyAudioDeviceThread(Context c, File f, OnCompletionListener completionListener) {
		this(c,f);
		System.out.println("MADT creating thread with file " + track.getAbsolutePath());
		setOnCompletionListener(completionListener);
	}
	
	public MyAudioDeviceThread(Context c, int resource, OnCompletionListener completionListener) {
		this(c,resource);
		System.out.println("MADT creating thread with resource " + resid);
		setOnCompletionListener(completionListener);
	}
	
	/*
	 * Methods
	 */
	
	
	//@see java.lang.Thread#run()
	//starts the thread
	public void run()
	{
		int defaultPriority = Thread.currentThread().getPriority();
		while (ButtonMatrix.notReady()) {
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			yield();
		}
		Thread.currentThread().setPriority(defaultPriority);
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
		if(!myAudio.isPlaying()) {
			myAudio.restart();
			myAudio.play();
		}
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
	
	/*public void setTrack(File f)
	{
		this.track = f;
		if (myAudio != null) {
            myAudio.quit();
        }
		myAudio = new MyAudioDevice(track);
	}*/ //Note: could be done, but we might as well just make new MADthreads

	public void setOnCompletionListener(OnCompletionListener completionListener) {
		// sets the completion listener for this media player
		myAudio.setOnCompletionListener(completionListener);
	}
	
	public void setStartTime(double start_time) {
		myAudio.setStartTime(start_time);
	}
	
	public double getStartTime() {
		return myAudio.getStartTime();
	}
	
	public void setEndTime(double end_time) {
		myAudio.setEndTime(end_time);
	}
	
	public double getEndTime() {
		return myAudio.getEndTime();
	}
	
	public void setVolume(double volume) {
		myAudio.setVolume(volume);
	}
	
	public double getVolume() {
		return myAudio.getVolume();
	}
	
	public void setPlaybackSpeed(double speed) {
		myAudio.setPlaybackSpeed(speed);
	}
	
	public double getPlaybackSpeed() {
		return myAudio.getPlaybackSpeed();
	}
	
	public void setBass(double bass) {
		myAudio.setBass(bass);
	}
	
	public double getBass() {
		return myAudio.getBass();
	}
	
	public void setTreble(double treble) {
		myAudio.setTreble(treble);
	}
	
	public double getTreble() {
		return myAudio.getTreble();
	}
	
	public double getTrackLength() {
		return myAudio.getTrackLength();
	}
	
	public String getTrackPath()
	{
		System.out.println("in gettp " + this.toString());
		if(track != null)
		{
			System.out.println("MADT: is not null");
			return track.getAbsolutePath();
		}
		else
			return "";
	}
	
	public void quit() {
		myAudio.quit();
	}

	public FileOrRes getFileOrRes() {
		if (myAudio.usingRes()) {
			return new FileOrRes(myAudio.getResid());
		}
		else {
			return new FileOrRes(myAudio.getFile());
		}
	}
}