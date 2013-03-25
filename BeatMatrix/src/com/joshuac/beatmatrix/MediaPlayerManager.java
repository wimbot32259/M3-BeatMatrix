
package com.joshuac.beatmatrix;

import android.content.Context;
import android.util.SparseArray;
import android.widget.Toast;
import java.io.File;

import com.joshuac.beatmatrix.MyAudioDevice.OnCompletionListener;


public class MediaPlayerManager 
{
	
	Context context;
	SparseArray<MyAudioDeviceThread> threads;
	Thread a;
	MyAudioDeviceThread b;
	private int total_buttons;
	private int[] mapped_buttons;

	MediaPlayerManager(Context c, int buttons){
		total_buttons = buttons;
		mapped_buttons = new int[total_buttons];
		for (int i = 0; i < total_buttons; i++) {
			mapped_buttons[i] = 0;
		}
		this.context = c;
		threads = new SparseArray<MyAudioDeviceThread>();
	}
	
	//i - id of the View
	//f - chosenFile (file chosen from 'ChooseFileDialog')
	public void setMapping(int i, File f, OnCompletionListener completionListener)
	{
		  mapped_buttons[i] = 1;
		  MyAudioDeviceThread thread = threads.get(i);
		  if(thread == null){
			  thread = new MyAudioDeviceThread(context, f, completionListener);
			  threads.put(i,thread);
			  run(i);
		  }
		  else {
			  thread.setTrack(f);
			  thread.setOnCompletionListener(completionListener);
		  }

		  String msg = f.getName();
		  Toast toast = Toast.makeText(context, msg + " mapped", Toast.LENGTH_SHORT);
		  toast.show();
	}//end setMapping
	
	//
	public void run(int i)
	{
			threads.get(i).start();
	}//end play
	
	public void play(int i)
	{
		threads.get(i).play();
	}
	
	public void loop(int i) {
		threads.get(i).loop();
	}
	
	public void pause(int i){
		if (mapped_buttons[i] == 1) {
			threads.get(i).pause();
		}
	}
	
	public void stopAll() {
		for (int i = 0; i < total_buttons; i++) {
			pause(i);
			//otherwise there's no song mapped to it, so no thread exists for it yet
			//(so it won't be playing anything)
		}
	}
	
	public int getTotalButtons() {
		return total_buttons;
	}
	
	public void setStartTime(double start_time, int i) {
		if (mapped_buttons[i] == 1) {
			threads.get(i).setStartTime(start_time);
		}
	}
	
	public void setEndTime(double end_time, int i) {
		if (mapped_buttons[i] == 1) {
			threads.get(i).setEndTime(end_time);
		}
	}
	
	public void setVolume(float volume, int i) {
		if (mapped_buttons[i] == 1) {
			threads.get(i).setVolume(volume);
		}
	}
	
	public void setPlaybackSpeed(double speed, int i) {
		if (mapped_buttons[i] == 1) {
			threads.get(i).setPlaybackSpeed(speed);
		}
	}

	public double getTrackLength(int i) {
		if (mapped_buttons[i] == 1) {
			return threads.get(i).getTrackLength();
		} else {
			return -1;
		}
	}

}
