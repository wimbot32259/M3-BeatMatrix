package com.joshuac.beatmatrix;

import android.media.AudioManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import java.io.File;

public class MediaPlayerThread extends Thread implements Runnable
{

	private volatile boolean playing;
	MediaPlayer mp;
	Context context;			//context of the application
	File track; 		//file descriptor of the track
	
	/*
	 * Constructor(s)
	 */
	
	public MediaPlayerThread(Context c, File f)
	{
	    super();
	    this.context = c;
	    this.track = f;
	}
	
	/*
	 * Methods
	 */
	
	//@see java.lang.Thread#run()
	//starts the thread
	public void run()
	{
		mp = new MediaPlayer();
		
		//Sets what audio stream to adjust volume for in-app
	//	setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		if (mp != null) {
            mp.reset();
            mp.release();
        }
		Uri ef = Uri.fromFile(track);
		mp = MediaPlayer.create(context, ef);
        //mp.start();
	}//end run
	
	//play/restarts the track
	public void play()
	{
		mp.setLooping(false);
		if(!mp.isPlaying())
		{
			mp.start();
		}
		else
		{
			mp.seekTo(0);
		}
	}
	
	//loops the track until tapped
	public void loop() {
		mp.setLooping(true);
		if (!mp.isPlaying()) {
			mp.start();
		} else {
			mp.seekTo(0);
		}
	}
	
	//pause the current track
	public void pause()
	{
		mp.setLooping(false);
		mp.seekTo(0);
		mp.pause();
	}
	
	/*
	 * Getters / Setters
	 */
	
	public void setTrack(File f)
	{
		this.track = f;
	}
	

}