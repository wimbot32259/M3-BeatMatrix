package com.joshuac.beatmatrix;

import java.io.File;

import com.joshuac.beatmatrix.MyAudioDevice.OnCompletionListener;
import com.joshuac.beatmatrix.SongEditDialog.OnSongEditSelectedListener;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Resources;
//import android.media.MediaPlayer;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.joshuac.beatmatrix.ChooseFileDialog.OnChooseFileSelectedListener;
import com.joshuac.beatmatrix.GestureListener.OnEditActionListener;

public class GestureListener extends GestureDetector.SimpleOnGestureListener{

	private BeatButton thisButton; 		//instance of the button
	private int buttonId = -1;				//id given to the button
	private static Resources resources = null;
	
	private static int editingButtonId;
	
	//Static Playing States
	private final static int WAITING = 0; 	//button is waiting to be played
	private final static int STOPPED = 1; 	//button is paused
	private final static int PLAYING = 2;	//button is playing once
	private final static int LOOPING = 3; 	//button is looping
	//Button Map State
	private boolean MAPPED = false;
	//Current button state
	//private int state = WAITING;
	
	//thread manager
	private static MediaPlayerManager manager = null;
	
	private OnCompletionListener soundListener;
	
	//media player
//	private MediaPlayer myMP = null;
	
	
	OnEditActionListener mCallback;
	
    public interface OnEditActionListener {
    	//System.out.println("action listener");
        public void onEditAction(int buttonId);
    }
	
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
		soundListener = new SoundCompletionListener((Activity) thisButton.getContext());
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
		//!ButtonMatrix.getMapButtonStatus()&& MAPPED && ButtonMatrix.getPlayButtonStatus()
    	if(!ButtonMatrix.getMapButtonStatus() && MAPPED && !ButtonMatrix.getEditButtonStatus())
    	{
			thisButton.changeState(PLAYING);
		    playButtonSound();
		    System.out.println("playing!!!!!!!!!!!!!!!!!!!!!!");
    	}
    	else if( ButtonMatrix.getMapButtonStatus() ) {
    		mapAction();
    	} else if (ButtonMatrix.getEditButtonStatus()) {
    		if (buttonId != -1) {
    			System.out.println(buttonId);
    			System.out.println("trying callback");
    			mCallback.onEditAction(buttonId);
    			System.out.println("Just called callback from gesturelistener");
    		}
    	}
		return true;
	}//end onSingleTapUp
	
	@Override
	//long press event
	public void onLongPress(MotionEvent e) 
	{
		//!ButtonMatrix.getMapButtonStatus()&& MAPPED && ButtonMatrix.getPlayButtonStatus()
    	if(MAPPED)
    	{
			thisButton.changeState(LOOPING);
	    	loopButtonSound();
    	}
	}//end onLongPress
	
	@Override
	//double tap event
	public boolean onDoubleTap(MotionEvent e)
	{

		return true;
	}//end onDoubleTap
	
	@Override
	//swipe event
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) 
	{
		//!ButtonMatrix.getMapButtonStatus()&& MAPPED && ButtonMatrix.getPlayButtonStatus()
    	if(MAPPED)
    	{
			thisButton.changeState(STOPPED);
			stopButtonSound();
    	}
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
		public class SoundCompletionListener extends OnCompletionListener
		{
			public SoundCompletionListener(Activity theActivity) {
				myActivity = theActivity;
			}
		    public void onCompletion()
		    {
		    	//go to stopped when sound complete
		    	soundEndAction();
		    }
		};
	
	
	private void mapAction() {
		File chosenFile = ButtonMatrix.getChosenFile();
		if(chosenFile != null)
		{
			manager.setMapping(buttonId, chosenFile, soundListener);
			thisButton.changeState(STOPPED);
			MAPPED = true;
		}
	}
	//WILL TODO fix these so edit screen knows which button we are working with

	
//	public void editAction() {
//		ButtonMatrix.setButtonId(buttonId);
//	}
	
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
	

    public void onAttach(Activity activity) {
        //super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
        	mCallback = (OnEditActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onEditActionListener");
        }
    }//end onAttach
    
    
    /*
     * 
     * Getters / Setters
     * 
     */
    
    public boolean getMapped()
    {
    	return MAPPED;
    }
    
	public void setMapped(boolean b)
	{
		MAPPED = b;
	}

	public void setTrack(File f)
	{
		manager.setMapping(buttonId, f, soundListener);
		thisButton.changeState(STOPPED);
		MAPPED = true;
	}
	
}//end GestureListener