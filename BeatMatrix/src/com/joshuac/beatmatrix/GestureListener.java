package com.joshuac.beatmatrix;

import java.io.File;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureListener extends GestureDetector.SimpleOnGestureListener{

	private BeatButton thisButton; 		//instance of the button
	private int buttonId;				//id given to the button
	private static Resources resources = null;
	
	//Static Playing States
	private final static int WAITING = 0; 	//button is waiting to be played
	private final static int STOPPED = 1; 	//button is paused
	private final static int PLAYING = 2;	//button is playing once
	private final static int LOOPING = 3; 	//button is looping
	//Button Map State
	private boolean MAPPED = false;
	//Current button state
	private int state = WAITING;
	
	//thread manager
	private static MediaPlayerManager manager = null;
	
	//media player
	private MediaPlayer myMP = null;
	
	/*
	 * Constructor(s)
	 */
	
	public GestureListener(Context c, BeatButton b, int i)
	{
		thisButton = b;
		buttonId = i;
		if(manager == null)
			manager = ButtonMatrix.getMediaPlayerManager();
		if(resources == null)
			resources = c.getResources();
	}
	
	/*
	 * Events
	 */
	
	@Override
	//single tap event
	//SMALL LATENCY - JOSH
	//change to SingleTapUp and remove DoubleTap for speed
	//single tap confirmed take a while to confirm, recommend remove double tap action
	public boolean onSingleTapConfirmed(MotionEvent e)
	{
    	if(!ButtonMatrix.getMapButtonStatus()&& MAPPED && ButtonMatrix.getPlayButtonStatus())
    	{
			thisButton.changeState(PLAYING);
		    playButtonSound();
    	}
    	else if( ButtonMatrix.getMapButtonStatus() )
    		mapAction();
		return true;
	}//end onSingleTapUp
	
	@Override
	//long press event
	public void onLongPress(MotionEvent e) 
	{
    	if(!ButtonMatrix.getMapButtonStatus()&& MAPPED && ButtonMatrix.getPlayButtonStatus())
    	{
			thisButton.changeState(LOOPING);
	    	loopButtonSound();
    	}
	}//end onLongPress
	
	@Override
	//double tap event
	public boolean onDoubleTap(MotionEvent e)
	{
    	if(!ButtonMatrix.getMapButtonStatus()&& MAPPED && ButtonMatrix.getPlayButtonStatus())
    	{
			thisButton.changeState(STOPPED);
			stopButtonSound();
    	}
		return true;
	}//end onDoubleTap
	
	@Override
	//swipe event
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) 
	{
		return true;
	}//end onFling
	
	@Override
	public void onShowPress(MotionEvent e) 
	{
		
	}//end onShowPress
	  
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
	{
	    return true;
	}//end onScroll
	  
	@Override
	public boolean onDown(MotionEvent e)
	{
	    return true;
	}//end onDown
	
	// User interaction methods
	
	//listen for song endings
		private OnCompletionListener soundListener = new OnCompletionListener()
		{
		    public void onCompletion(MediaPlayer mp)
		    {
		    	//go to stopped when sound complete
		    	soundEndAction();
		    }
		};
	
	
	private void mapAction() {
		File chosenFile = ButtonMatrix.getChosenFile();
		if(chosenFile != null)
		{
			myMP = manager.setMapping(buttonId, chosenFile);
			myMP.setOnCompletionListener(soundListener);
			thisButton.changeState(STOPPED);
			MAPPED = true;
		}
	}
	
	public void soundEndAction() {
		//This may be used by another object when a sound ends
		thisButton.changeState(STOPPED);
	}
	
	
	// Media player interface methods
	
	private void playButtonSound() {
		//Call this to play a sound once
    	manager.play(buttonId);
	}
	
	private void stopButtonSound() {
		//Call this to stop a sound
		manager.pause(buttonId);
	}
	
	private void loopButtonSound() {
		//Call this to loop a sound
    	manager.loop(buttonId);
	}

	

}//end GestureListener