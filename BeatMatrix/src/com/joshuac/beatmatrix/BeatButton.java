package com.joshuac.beatmatrix;

import java.io.File;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class BeatButton extends ImageButton
{
	
	private BeatButton thisButton; 		//instance of this current button
	private int buttonId;				//buttons are added dynamically so they have -1 as getId() 
	private static int currentId = 0; 	//current id to delegate
	
	//Static Playing States
	private final static int WAITING = 0; 	//button is waiting to be played
	private final static int STOPPED = 1; 	//button is not playing
	private final static int PLAYING = 2;	//button is playing once
	private final static int LOOPING = 3; 	//button is looping
	
	//Button Map State
	private boolean MAPPED = false;
	
	//Current button state
	private int state = WAITING;
	
	//thread manager
	private static MediaPlayerManager manager;
	
	private MediaPlayer myMP = null;
	
	//double tap vars
	long lastPressTime = 0;
	private static final long DOUBLE_PRESS_INTERVAL = 400;
	
	//gesture detector
	GestureDetector mGestureDetector;

	/*
	 * 	Constructors
	 */
	
	public BeatButton(Context context)
	{
		super(context);
		thisButton = this;
		
		//set listeners
		//this.setOnClickListener(clickListener);
		//this.setOnLongClickListener(longClickListener);
		
		//set thread manager
		manager = ButtonMatrix.getMediaPlayerManager();
		this.buttonId = currentId;
		currentId = (currentId+1)%manager.getTotalButtons();
		mGestureDetector = new GestureDetector(context, new GestureListener(context, this, buttonId));
	}
	
	public BeatButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		thisButton = this;
		
		//set listeners
		//this.setOnClickListener(clickListener);
		//this.setOnLongClickListener(longClickListener);
		
		//set thread manager
		manager = ButtonMatrix.getMediaPlayerManager();
		this.buttonId = currentId++;
		currentId = (currentId+1)%manager.getTotalButtons();
		mGestureDetector = new GestureDetector(context, new GestureListener(context, this, buttonId));
	}
	
	public BeatButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		thisButton = this;
		
		//set listeners
		//this.setOnClickListener(clickListener);
		//this.setOnLongClickListener(longClickListener);
		
		//set thread manager
		manager = ButtonMatrix.getMediaPlayerManager();
		this.buttonId = currentId++;
		currentId = (currentId+1)%manager.getTotalButtons();
		mGestureDetector = new GestureDetector(context, new GestureListener(context, this, buttonId));
	}

	/*
	 * 	Listeners 
	 */
	
	@Override
	//let gesture detector handle motion events
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return true;
	}
	
	public void stop() {
		if (state != WAITING) {
			changeState(STOPPED);
			stopButtonSound();
		}
	}
	
	private void stopButtonSound() {
		//Call this to stop a sound
		manager.pause(buttonId);
	}
	
	public void changeState(int newState) {
		//Call this to change button state and image
		state = newState;
		
		Drawable newImg;
		
		if (state == WAITING) {
			newImg = getResources().getDrawable(R.drawable.graybutton);
		}
		else if (state == PLAYING) {
			newImg = getResources().getDrawable(R.drawable.greenbutton);
			//newImg = getResources().getDrawable(R.drawable.playonce);
		}
		else if (state == LOOPING) {
			newImg = getResources().getDrawable(R.drawable.redbutton);
		}
		else if (state == STOPPED){
			newImg = getResources().getDrawable(R.drawable.yellowbutton);
		}
		else {
			newImg = getResources().getDrawable(R.drawable.graybutton);
		}
		
		if (newImg instanceof TransitionDrawable) {
			TransitionDrawable transition = (TransitionDrawable)newImg;			
		    thisButton.setImageDrawable(transition);
		    transition.startTransition(400);
		}
		else {
			thisButton.setImageDrawable(newImg);
		}
	}
	
	/*
	 * ORIGINAL LISTENERS
	 * 
	//listen for clicks
	private OnClickListener clickListener = new OnClickListener()
	{
	    public void onClick(View v) {
	    	
	    	//Do something when the button is clicked

	    	if(!ButtonMatrix.getMapButtonStatus()&& MAPPED && ButtonMatrix.getPlayButtonStatus())
	    	{	
	    		//Play music
		    	long pressTime = System.currentTimeMillis();
		    	if (pressTime - lastPressTime <= DOUBLE_PRESS_INTERVAL)
		    	{	//button was double tapped
		    		doubleTapAction();
		    	}
		    	else {
		    		//button was single tapped
		    		singleTapAction();
		    	}
		    	lastPressTime = pressTime;
	    	}//end play music
	    	else if( ButtonMatrix.getMapButtonStatus() )
	    	{ 	//map button is on
	    		mapAction();
	    	}
	    }
	}; //end onClickListener

	//listen for long clicks
	private OnLongClickListener longClickListener = new OnLongClickListener()
	{
	    public boolean onLongClick(View v)
	    {
	    	// do something when the button is long clicked
	    	holdAction();
	    	return true;
	    }
	};

	//listen for song endings
	private OnCompletionListener soundListener = new OnCompletionListener()
	{
	    public void onCompletion(MediaPlayer mp)
	    {
	    	//go to stopped when sound complete
	    	soundEndAction();
	    }
	};
	
	// User interaction methods
	
	private void doubleTapAction() {
		changeState(STOPPED);
		stopButtonSound();
	}
	
	private void singleTapAction() {
		changeState(PLAYING);
	    playButtonSound();
		
	}
	
	private void holdAction() {
		changeState(LOOPING);
    	loopButtonSound();
	}
	
	private void mapAction() {
		File chosenFile = ButtonMatrix.getChosenFile();
		if(chosenFile != null)
		{
			myMP = manager.setMapping(buttonId, chosenFile);
			myMP.setOnCompletionListener(soundListener);
			changeState(STOPPED);
			MAPPED = true;
		}
	}
	
	public void soundEndAction() {
		//This may be used by another object when a sound ends
		changeState(STOPPED);
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
	
	// State change method
	
	private void changeState(int newState) {
		//Call this to change button state and image
		state = newState;
		
		Drawable newImg;
		
		if (state == WAITING) {
			newImg = getResources().getDrawable(R.drawable.graybutton);
		}
		else if (state == PLAYING) {
			newImg = getResources().getDrawable(R.drawable.greenbutton);
			//newImg = getResources().getDrawable(R.drawable.playonce);
		}
		else if (state == LOOPING) {
			newImg = getResources().getDrawable(R.drawable.redbutton);
		}
		else if (state == STOPPED){
			newImg = getResources().getDrawable(R.drawable.yellowbutton);
		}
		else {
			newImg = getResources().getDrawable(R.drawable.graybutton);
		}
		
		if (newImg instanceof TransitionDrawable) {
			TransitionDrawable transition = (TransitionDrawable)newImg;			
		    thisButton.setImageDrawable(transition);
		    transition.startTransition(400);
		}
		else {
			thisButton.setImageDrawable(newImg);
		}
	}
	*/
	
	/*
	 * Getters / Setters
	 */
	

		
}//end class BeatButton
