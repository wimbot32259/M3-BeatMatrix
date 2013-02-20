package com.joshuac.beatmatrix;

import java.io.File;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

public class BeatButton extends ImageButton
{
	
	private BeatButton thisButton; 		//instance of this current button
	private int buttonId;				//buttons are added dynamically so they have -1 as getId() 
	private static int currentId = 1; 	//current id to delegate
	
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
	
	//double tap vars
	long lastPressTime = 0;
	private static final long DOUBLE_PRESS_INTERVAL = 400;

	/*
	 * 	Constructors
	 */
	
	public BeatButton(Context context)
	{
		super(context);
		thisButton = this;
		
		//set listeners
		this.setOnClickListener(clickListener);
		this.setOnLongClickListener(longClickListener);
		
		//set thread manager
		manager = ButtonMatrix.getMediaPlayerManager();
		this.buttonId = currentId++;
	}
	
	public BeatButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		thisButton = this;
		
		//set listeners
		this.setOnClickListener(clickListener);
		this.setOnLongClickListener(longClickListener);
		
		//set thread manager
		manager = ButtonMatrix.getMediaPlayerManager();
		this.buttonId = currentId++;
	}
	
	public BeatButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		thisButton = this;
		
		//set listeners
		this.setOnClickListener(clickListener);
		this.setOnLongClickListener(longClickListener);
		
		//set thread manager
		manager = ButtonMatrix.getMediaPlayerManager();
		this.buttonId = currentId++;
	}

	/*
	 * 	Listeners 
	 */
	
	//listen for clicks
	private OnClickListener clickListener = new OnClickListener()
	{
	    public void onClick(View v) {
	    	
	    	//Do something when the button is clicked
<<<<<<< HEAD
	    	if(!ButtonMatrix.getMapButtonStatus()&& MAPPED && ButtonMatrix.getPlayButtonStatus())
=======
	    	if(!ButtonMatrix.getMapButtonStatus() && MAPPED && ButtonMatrix.getPlayButtonStatus())
>>>>>>> edb4207d6271be28779081a94045d2f8a8a436a3
	    	{	
	    		//Play music
		    	long pressTime = System.currentTimeMillis();
		    	if (pressTime - lastPressTime <= DOUBLE_PRESS_INTERVAL)
		    	{	
		    		//Button was double tapped
		    		System.out.println("double tap");
		    		//TransitionDrawable transition = (TransitionDrawable)
		    	    //        getResources().getDrawable(R.drawable.endplayonce);
		    	    //thisButton.setImageDrawable(transition);
		    	    //transition.startTransition(400);
	    			thisButton.setImageDrawable(getResources().getDrawable(R.drawable.yellowbutton));
		    		manager.pause(buttonId);
		    	}
		    	else if(state == WAITING)
		    	{
		    		state = PLAYING;
		    		//TransitionDrawable transition = (TransitionDrawable)
		    	   //         getResources().getDrawable(R.drawable.playonce);
		    	    //thisButton.setImageDrawable(transition);
		    	   // transition.startTransition(400);
	    			thisButton.setImageDrawable(getResources().getDrawable(R.drawable.greenbutton));
		    	    manager.play(buttonId);
		    	}
		    	else if(state==PLAYING || state==LOOPING)
		    	{
		    		//TransitionDrawable transition = (TransitionDrawable)
		    	    //        getResources().getDrawable(R.drawable.playonce);
		    	    //thisButton.setImageDrawable(transition);
		    	    //transition.startTransition(400);
	    			thisButton.setImageDrawable(getResources().getDrawable(R.drawable.greenbutton));
		    	    manager.play(buttonId);
		    	}
		    	lastPressTime = pressTime;
	    	}//end play music
	    	else if( ButtonMatrix.getMapButtonStatus() )
	    	{ 	
	    		//Map button is on
	    		File chosenFile = ButtonMatrix.getChosenFile();
	    		if(chosenFile != null)
	    		{
	    			manager.setMapping(buttonId, chosenFile);
	    			thisButton.setImageDrawable(getResources().getDrawable(R.drawable.yellowbutton));
	    			MAPPED = true;
	    			manager.run(buttonId);
	    		}
	    	}
	    }
	}; //end onClickListener

	//listen for long clicks
	private OnLongClickListener longClickListener = new OnLongClickListener()
	{
	    public boolean onLongClick(View v)
	    {
	    	// do something when the button is long clicked
	    	state = LOOPING;
			thisButton.setImageDrawable(getResources().getDrawable(R.drawable.redbutton));
			//thisButton.setImageResource(R.drawable.greenbutton);
	    	manager.loop(buttonId);
	    	return true;
	    }
	};
	
	/*
	 * Getters / Setters
	 */
	

		
}//end class BeatButton
