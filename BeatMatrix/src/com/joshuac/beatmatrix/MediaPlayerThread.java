package com.joshuac.beatmatrix;



import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;

public class MediaPlayerThread extends Thread implements Runnable
{

	private boolean playing;
	private boolean running = false;
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
	    Toast.makeText(context, f.getName() + " mapped", Toast.LENGTH_SHORT).show();
	}
	
	/*
	 * Methods
	 */
	
	//@see java.lang.Thread#run()
	//starts the thread
	public void run()
	{
		running = true;
		mp = new MediaPlayer();
		if (mp != null) {
            mp.reset();
            mp.release();
        }
		Uri ef = Uri.fromFile(track);
		mp = MediaPlayer.create(context, ef);
        
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
		Toast.makeText(context, f.getName() + " mapped", Toast.LENGTH_SHORT).show();
	}
	
	/*
	 * Getters / Setters
	 */

	public boolean isRunning()
	{
		return running;
	}
}