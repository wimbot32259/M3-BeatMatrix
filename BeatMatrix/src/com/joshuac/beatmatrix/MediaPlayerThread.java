package com.joshuac.beatmatrix;



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
		if(!mp.isPlaying())
		{
			mp.start();
		}
		else
		{
			mp.seekTo(0);
		}
	}
	
	//pause the current track
	public void pause()
	{
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