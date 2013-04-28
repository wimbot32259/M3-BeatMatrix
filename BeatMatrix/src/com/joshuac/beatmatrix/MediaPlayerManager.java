
package com.joshuac.beatmatrix;

import android.content.Context;
import android.widget.Toast;

import com.joshuac.beatmatrix.ChooseFileDialog.FileOrRes;
import com.joshuac.beatmatrix.MyAudioDevice.OnCompletionListener;


public class MediaPlayerManager 
{
	
	Context context;
	MyAudioDeviceThread[] threads = null;
	private int total_buttons;
	private int[] mapped_buttons;
	//private String[] filepaths;

	MediaPlayerManager(Context c, int buttons){
		System.out.println("creating new manager");
		total_buttons = buttons;
		mapped_buttons = new int[total_buttons];
		//filepaths = new String[total_buttons];
		threads = new MyAudioDeviceThread[total_buttons];
		for (int i = 0; i < total_buttons; i++) {
			mapped_buttons[i] = 0;
			//filepaths[i] = "";
			threads[i] = null;
		}
		this.context = c;
	}
	
	//i - id of the ViewS
	//f - chosenFile (file chosen from 'ChooseFileDialog')
	public void setMapping(int i, FileOrRes chosenFile, OnCompletionListener completionListener)
	{
		//System.out.println("mapping " + chosenFile.getAbsolutePath());
		mapped_buttons[i] = 1;
		//filepaths[i] = chosenFile.getAbsolutePath();
		MyAudioDeviceThread thread = threads[i];
		if(thread == null){
			//System.out.println("manager: creating new thread for " + i);
			if (chosenFile.isFile()) {
				thread = new MyAudioDeviceThread(context, chosenFile.getFile(), completionListener);
			}
			else {
				thread = new MyAudioDeviceThread(context, chosenFile.getResid(), completionListener);
			}
			threads[i] = thread;
			//System.out.println("thread " + i + "created: " + threads[i].getTrackPath());
			//ButtonMatrix.setPath(i, chosenFile.getAbsolutePath());
			run(i);
		}
		else 
		{
			//System.out.println("manager: thread exists for " + i);
			//System.out.println("manager: quitting thread for " + i);
			threads[i].quit();
			try {
				threads[i].join();
				System.out.println("manager: starting new thread for " + i);
				if (chosenFile.isFile()) {
					thread = new MyAudioDeviceThread(context, chosenFile.getFile(), completionListener);
				}
				else {
					thread = new MyAudioDeviceThread(context, chosenFile.getResid(), completionListener);
				}
				threads[i] = thread;
				System.out.println("thread " + i + "created: " + threads[i].getTrackPath());
				//ButtonMatrix.setPath(i, chosenFile.getAbsolutePath());
				run(i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		String msg = chosenFile.getName();
		Toast toast = Toast.makeText(context, msg + " mapped", Toast.LENGTH_SHORT);
		toast.show();
	}//end setMapping

	public void run(int i)
	{
			threads[i].start();
	}//end play
	
	
	
	public void play(int i)
	{
		System.out.println("calling play " + i);
		System.out.println((threads[i]==null) + " " + this);
		if (mapped_buttons[i] == 1) {
			threads[i].play();
		}
	}
	
	public void loop(int i) {
		if (mapped_buttons[i] == 1) {
			threads[i].loop();
		}
	}
	
	public void pause(int i){
		if (mapped_buttons[i] == 1) {
			threads[i].pause();
		}
	}
	
	public void stopThread(int i) {
		if(threads[i] != null){
			threads[i].quit();
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			threads[i] = null;
			mapped_buttons[i] = 0;
		}
	}
	
	/*
	 * 
	 * Getters / Setters
	 * 
	 */
	
	public int getTotalButtons() {
		return total_buttons;
	}
	
	public void setStartTime(double start_time, int i) {
		if (mapped_buttons[i] == 1) {
			threads[i].setStartTime(start_time);
		}
	}
	
	public double getStartTime(int i) {
		if (mapped_buttons[i] == 1) {
			return threads[i].getStartTime();
		} else {
			return 0;
		}
	}
	
	public void setEndTime(double end_time, int i) {
		if (mapped_buttons[i] == 1) {
			threads[i].setEndTime(end_time);
		}
	}
	
	public double getEndTime(int i) {
		if (mapped_buttons[i] == 1) {
			return threads[i].getEndTime();
		} else {
			return 0;
		}
	}
	
	public void setVolume(double volume, int i) {
		if (mapped_buttons[i] == 1) {
			threads[i].setVolume(volume);
		}
	}
	
	public double getVolume(int i) {
		if (mapped_buttons[i] == 1) {
			return threads[i].getVolume();
		} else {
			return 0;
		}
	}
	
	public void setPlaybackSpeed(double speed, int i) {
		if (mapped_buttons[i] == 1) {
			threads[i].setPlaybackSpeed(speed);
		}
	}
	
	public double getPlaybackSpeed(int i) {
		if (mapped_buttons[i] == 1) {
			return threads[i].getPlaybackSpeed();
		} else {
			return 0;
		}
	}

	public double getTrackLength(int i) {
		if (mapped_buttons[i] == 1) {
			return threads[i].getTrackLength();
		} else {
			return -1;
		}
	}
	
	public void setBass(double bass, int i) {
		if (mapped_buttons[i] == 1) {
			threads[i].setBass(bass);
		}
	}
	
	public double getBass(int i) {
		if (mapped_buttons[i] == 1) {
			return threads[i].getBass();
		} else {
			return 0;
		}
	}
	
	public void setTreble(double treble, int i) {
		if (mapped_buttons[i] == 1) {
			threads[i].setTreble(treble);
		}
	}
	
	public double getTreble(int i) {
		if (mapped_buttons[i] == 1) {
			return threads[i].getTreble();
		} else {
			return 0;
		}
	}
	
	public String getTrackPath(int i)
	{
		if(threads[i] != null){
			//System.out.println("thread " + i + " is not null");
			return threads[i].getTrackPath();
		}
		else return "";
	}

	public FileOrRes getFileOrRes(int i) {
		System.out.println("calling getFOR " + i);
		System.out.println((threads[i]==null) + " " + this);
		if(threads[i] != null){
			System.out.println("Thread "+ i +" is not null, thread's FOR is null? " + (threads[i].getFileOrRes()==null));
			return threads[i].getFileOrRes();
		}
		else return null;
	}

}

/*public void stopAll() {
	for (int i = 0; i < total_buttons; i++) {
		pause(i);
		//otherwise there's no song mapped to it, so no thread exists for it yet
		//(so it won't be playing anything)
	}
}

//kills thread
public void kill(int i)
{
	if(threads[i] != null)
		threads[i].interrupt();
	threads[i] = null;
}

public void resetThreads()
{
	for (int i = 0; i < total_buttons; i++)
	{
		kill(i);
		threads[i] = null;
	}
}*/
