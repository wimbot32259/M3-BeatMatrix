package com.joshuac.beatmatrix;

import android.content.Context;
import android.util.SparseArray;
import android.widget.Toast;
import java.io.File;


public class MediaPlayerManager 
{
	
	Context context;
	SparseArray<MediaPlayerThread> threads;
	Thread a;
	MediaPlayerThread b;

	MediaPlayerManager(Context c){
		this.context = c;
		threads = new SparseArray<MediaPlayerThread>();
	}
	
	//i - id of the View
	//f - chosenFile (file chosen from 'ChooseFileDialog')
	//maps the thread with a file to play
	//call run() after to actually start the thread
	public void setMapping(int i, File f)
	{		  
		  MediaPlayerThread thread = threads.get(i);
		  if(thread == null){
			  System.out.println("here");
			  thread = new MediaPlayerThread(context, f);
			  threads.put(i,thread);
		  }
		  else
			  thread.setTrack(f);

	}//end setMapping
	
	//
	public void run(int i)
	{
		if( !threads.get(i).isRunning() )
			threads.get(i).start();
	}//end play
	
	public void play(int i)
	{
		threads.get(i).play();
	}
	
	public void pause(int i){
		threads.get(i).pause();
	}

}
