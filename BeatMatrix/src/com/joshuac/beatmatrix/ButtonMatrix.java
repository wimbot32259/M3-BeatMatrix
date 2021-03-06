package com.joshuac.beatmatrix;


import android.app.Activity;
import android.app.DialogFragment;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import java.io.File;



public class ButtonMatrix extends Activity implements ChooseFileDialog.OnChooseFileSelectedListener
{
	private static File chosenFile; 	//file chosen to map
	
	
	private static boolean playButtonOn = false; 	//is the play button on?
	private static boolean mapButtonOn = false; 	// is the map button on?
	
	final int NUM_BUTTONS = 5; 	//number of buttons
	final int NUM_ROWS = 5;    	//number of rows (for the buttons)
	
	//references to the actual buttons
	private ImageView playButton;
	private ImageView chooseButton;
	private ImageView mapButton;
	
	private static MediaPlayerManager manager; //manages the music threads
	
	//enables ButtonMatrix to communicate with the ChooseFileDialog
	//called when user selects a File from the storage device
	//now do something with the File...
	public void onFileSelected(File f)
	{
		chosenFile = f;
		chooseButton.setImageDrawable(getResources().getDrawable(R.drawable.playlist_off));
		Toast toast = Toast.makeText(getApplicationContext(), chosenFile.getName(), Toast.LENGTH_SHORT);
		toast.show();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.button_matrix_activity);
        
		manager = new MediaPlayerManager(this);
        
		
		//dynamically add TableRows and BeatButtons
		TableLayout bmh = (TableLayout) findViewById(R.id.beatMatrixHolder);
		for(int i = 0; i < NUM_ROWS; i++)
		{
			//init new row, layout params, and gravity
			TableRow trow = new TableRow(this);
			TableLayout.LayoutParams lp = new TableLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			trow.setGravity(Gravity.CENTER);
			//add row
			bmh.addView(trow, lp);
			
			//add BeatButtons
			for(int j = 0; j < NUM_BUTTONS; j++)
			{
				//init button and set scaling
				BeatButton newButton = (BeatButton) getLayoutInflater().inflate(R.layout.beat_button, null);
				newButton.setScaleType(ScaleType.FIT_XY);
				//create layout BeatButton params
				Display display = getWindowManager().getDefaultDisplay();
				Point point = new Point();
				display.getSize(point);
				//dynamic width
				int width = (point.y)/(2*NUM_BUTTONS);
				TableRow.LayoutParams parms = new TableRow.LayoutParams(width,width);
				trow.addView(newButton,parms);
			}
		}//end adding View items dynamically
		
		//everything here will be added below the beat matrix
		
		//add layer of row(s) below BeatButton matrix
		//init new row, layout params, and gravity
		TableRow trow_extra1 = new TableRow(this);
		TableLayout.LayoutParams lp = new TableLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		trow_extra1.setGravity(Gravity.CENTER);
		//add row
		bmh.addView(trow_extra1, lp);
		
		//create layout ImageView params
		Display display = getWindowManager().getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		//dynamic width
		int width = (point.y)/(2*NUM_BUTTONS);
		TableRow.LayoutParams parms = new TableRow.LayoutParams(width,width);
		
		//init play ImageView
		playButton = new ImageView(this);
		playButton.setImageDrawable(getResources().getDrawable(R.drawable.playicon_off));
		playButton.setScaleType(ScaleType.FIT_XY);
		//add file chooser ImageView to extra row
		trow_extra1.addView(playButton,parms);
		
		//init file chooser ImageView
		chooseButton = new ImageView(this);
		chooseButton.setImageDrawable(getResources().getDrawable(R.drawable.playlist_off));
		chooseButton.setScaleType(ScaleType.FIT_XY);
		//add file chooser ImageView to extra row
		trow_extra1.addView(chooseButton,parms);
		
		//init map button ImageView
		mapButton = new ImageView(this);
		mapButton.setImageDrawable(getResources().getDrawable(R.drawable.mapbutton_off));
		mapButton.setScaleType(ScaleType.FIT_XY);
		//add file chooser ImageView to extra row
		trow_extra1.addView(mapButton,parms);
		
		
		/*
		 * Add Listeners
		 */
		
		//register the play button's click listener
		//responsible for changing this button's image on click
		playButton.setOnClickListener(new OnClickListener()
		{
            public void onClick(View v)
            {
            	ImageView t = (ImageView) v;
            	if(!playButtonOn)
            	{
            		//turn play button on w/ transition
	        		TransitionDrawable transition = (TransitionDrawable)
		    	            getResources().getDrawable(R.drawable.turn_play_on);
		    	    t.setImageDrawable(transition);
		    	    transition.startTransition(400);
		    	    playButtonOn = true;
		    	    //turn map button off
		    	    mapButton.setImageDrawable(getResources().getDrawable(R.drawable.mapbutton_off));
            		mapButtonOn = false;
            	}
            	else
            	{
            		t.setImageDrawable(getResources().getDrawable(R.drawable.playicon_off));
            		playButtonOn = false;
            	}
            	
            	
            }
        });
		
		//register the choose-file-dialog-button's click method
		//responsible for changing this button's image on click
		chooseButton.setOnClickListener(new OnClickListener()
		{
            public void onClick(View v)
            {
            	ImageView t = (ImageView) v;
        		TransitionDrawable transition = (TransitionDrawable)
	    	            getResources().getDrawable(R.drawable.turn_playlist_on);
	    	    t.setImageDrawable(transition);
	    	    transition.startTransition(400);
            	
	    	    showChooseFileDialog();
            	
            }//end onClick
        });
		
		
		//register the map button's click listener
		//responsible for changing this button's image on click
		mapButton.setOnClickListener(new OnClickListener()
		{
            public void onClick(View v)
            {
            	ImageView t = (ImageView) v;

            	if(!mapButtonOn){
            		//turn map button on w/ transition
                	TransitionDrawable transition;
	        		transition = (TransitionDrawable)
		    	            getResources().getDrawable(R.drawable.turn_map_on);
	            	t.setImageDrawable(transition);
		    	    transition.startTransition(400);
	        		mapButtonOn = true;
	        		//turn play button off
		    	    playButton.setImageDrawable(getResources().getDrawable(R.drawable.playicon_off));
            		playButtonOn = false;
	        		
            	}
            	else{
            		t.setImageDrawable(getResources().getDrawable(R.drawable.mapbutton_off));
            		mapButtonOn = false;
            	}
	    	    
            }//end onClick
        });
		
	}//end onCreate

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_button_matrix, menu);
		return true;
	}
	

	//show the choose file dialog
	//set context and title, then show
	void showChooseFileDialog()
	{
		ChooseFileDialog.setContext(this);
		DialogFragment newFragment = ChooseFileDialog.newInstance(R.string.chooseFileDialogTitle);
	    newFragment.show(getFragmentManager(), "dialog");
	 }
	
	//DELETE test
	void playAudio2()
	{
		String[] STAR = { "*" };     
		Uri allaudiosong = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		String audioselection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
		Cursor cursor;
		cursor = managedQuery(allaudiosong, STAR, audioselection, null, null);

		if (cursor != null) {
		    if (cursor.moveToFirst()) {
		        do {
		           
		            String fullpath = cursor.getString(cursor
		                    .getColumnIndex(MediaStore.Audio.Media.DATA));
		            System.out.println("Audio Song FullPth= "+fullpath); 
		            Toast toast = Toast.makeText(getApplicationContext(), fullpath, Toast.LENGTH_SHORT);
		    		toast.show();

		        } while (cursor.moveToNext());
		    }
		}
	}//end playAudio2
	
	
	/*
	 * Getters / Setters
	 */
	
	public static boolean getMapButtonStatus()
	{
		return mapButtonOn; 
	}
	
	public static boolean getPlayButtonStatus()
	{
		return playButtonOn;
	}
	
	public static MediaPlayerManager getMediaPlayerManager()
	{
		return manager;
	}
	
	public static File getChosenFile()
	{
		return chosenFile;
	}

}//end class ButtonMatrix
