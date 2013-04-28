package com.joshuac.beatmatrix;

import java.io.File;
import java.util.ArrayList;

import com.joshuac.beatmatrix.ChooseFileDialog.FileOrRes;
import com.joshuac.beatmatrix.GestureListener.SoundCompletionListener;
import com.joshuac.beatmatrix.GestureListener.OnEditActionListener;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Environment;
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
//import android.database.Cursor;
//import android.net.Uri;
//import android.provider.MediaStore;
//import java.util.List;



public class ButtonMatrix extends Activity implements ChooseFileDialog.OnChooseFileSelectedListener, SongEditDialog.OnSongEditSelectedListener, SongSelectDialog.OnSongSelectedListener, GestureListener.OnEditActionListener
{
	private static FileOrRes chosenFile; 	//file chosen to map
	
	private static int editingButtonId;
	static boolean waitingId = true;
	boolean CHOOSING = false;
	
	private static boolean playButtonOn = false; 	//is the play button on?
	private static boolean mapButtonOn = false; 	// is the map button on?
	private static boolean editButtonOn = false; 	// is the edit button on?
	
	final int NUM_COLUMNS = 5; 	//number of buttons
	final int NUM_ROWS = 5;    	//number of rows (for the buttons)
	final int TOTAL_BUTTONS = NUM_COLUMNS*NUM_ROWS;
	//add scaling for sprint 2? ^^^
	
	//references to the actual buttons
	private ImageView playButton;
	private ImageView stopButton;
	//private ImageView chooseButton;
	private ImageView mapButton;
	private DialogFragment newFragment;
	private ImageView editButton;
	
	//choose file dialog
	private DialogFragment chooseFileDialog;
	
	//List of existing beat buttons
	private ArrayList<BeatButton> buttonList;
	
	private static MediaPlayerManager manager; //manages the music threads
	
	//static values for saving/restoring state
	 public static final String PREFS_NAME = "BeatMatrixPreferences";
	 //public static String PREFS_BUTTON_STATE = "BeatMatrixButtonState";
	 public static String PREFS_BUTTON_MAPPED = "BeatMatrixButtonMapped";
	 public static String PREFS_BUTTON_ISFILE = "BeatMatrixButtonIsFile";
	 public static String PREFS_BUTTON_FILENAME = "BeatMatrixButtonFilename";
	 public static String PREFS_BUTTON_RESID = "BeatMatrixButtonResid";
	 //private static String[] paths;
	 private static boolean restored = false;
	 
	//static button playing states
	private final static int WAITING = 0; 	//button is waiting to be played
	private final static int STOPPED = 1; 	//button is not playing	
	
	//enables ButtonMatrix to communicate with the ChooseFileDialog
	//called when user selects a File from the storage device
	//now do something with the File...
	public void onFileSelected(FileOrRes f)
	{
		chosenFile = f;
		//chooseButton.setImageDrawable(getResources().getDrawable(R.drawable.playlist_off)); 
		//Consider deleting
		Toast toast = Toast.makeText(getApplicationContext(), chosenFile.getName(), Toast.LENGTH_SHORT);
		toast.show();
    }
	
	public void onSongSelected(int buttonId) {
		editingButtonId = buttonId;
		//chooseButton.setImageDrawable(getResources().getDrawable(R.drawable.playlist_off)); 
		//Consider deleting
		Toast toast = Toast.makeText(getApplicationContext(), Integer.toString(buttonId), Toast.LENGTH_SHORT);
		toast.show();
		showSongEditDialog();
    }
	
	@Override
	public void onEditAction(int buttonId) {
		System.out.println("in buttonmatrix, callback just got called");
		editingButtonId = buttonId;
		waitingId = false;
		System.out.println("test");
		showSongEditDialog();
	}
	
	public void onEditInfoSelected(double start_time, double end_time, double volume, double speed, double bass, double treble) {
		
		//chooseButton.setImageDrawable(getResources().getDrawable(R.drawable.playlist_off)); 
		//Consider deleting
//		Toast toast = Toast.makeText(getApplicationContext(), ""+start_time+" "+end_time+" "+volume+" "+speed/*Double.toString(start_time)*/, Toast.LENGTH_SHORT);
//		toast.show();

		//now call setters for audio device
		if (end_time > start_time) {
			manager.setStartTime(start_time, editingButtonId);
			manager.setEndTime(end_time, editingButtonId);
		}
		if (volume <= 0) {
			manager.setVolume(1, editingButtonId);
		} else {
			manager.setVolume(volume, editingButtonId);
		}
		if (speed <= 0 || speed > 4) {
			manager.setPlaybackSpeed(1, editingButtonId);
		} else {
			manager.setPlaybackSpeed(speed, editingButtonId);
		}
		manager.setBass(bass, editingButtonId);
		manager.setTreble(treble, editingButtonId);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("in onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.button_matrix_activity);

		manager = new MediaPlayerManager(this, TOTAL_BUTTONS);
		buttonList = new ArrayList<BeatButton>(TOTAL_BUTTONS);
		//paths = new String[TOTAL_BUTTONS];
		
		//for(int i = 0; i < TOTAL_BUTTONS; i++)
		//	paths[i] = "";
		
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
			for(int j = 0; j < NUM_COLUMNS; j++)
			{
				//init button and set scaling
				BeatButton newButton = (BeatButton) getLayoutInflater().inflate(R.layout.beat_button, null);
				newButton.onAttach(this);
				buttonList.add(newButton);
				
				//reset current id when building each time
				if(i==0 && j==0)
					newButton.resetCurrentId();
				
				newButton.setScaleType(ScaleType.FIT_XY);
				//create layout BeatButton params
				Display display = getWindowManager().getDefaultDisplay();
				Point point = new Point();
				display.getSize(point);
				//dynamic width
				int width = (point.y)/(2*NUM_COLUMNS);
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
		int width = (point.y)/(2*NUM_COLUMNS);
		TableRow.LayoutParams parms = new TableRow.LayoutParams(width,width);
		
		//init play ImageView
		playButton = new ImageView(this);
		playButton.setImageDrawable(getResources().getDrawable(R.drawable.playicon_off));
		playButton.setScaleType(ScaleType.FIT_XY);
		//add file chooser ImageView to extra row
		trow_extra1.addView(playButton,parms);
		
		//init stop ImageView
		stopButton = new ImageView(this);
		stopButton.setImageDrawable(getResources().getDrawable(R.drawable.stopicon_off));
		stopButton.setScaleType(ScaleType.FIT_XY);
		//add file chooser ImageView to extra row
		trow_extra1.addView(stopButton,parms);
		
		/*
		//init file chooser ImageView
		chooseButton = new ImageView(this);
		chooseButton.setImageDrawable(getResources().getDrawable(R.drawable.playlist_off));
		chooseButton.setScaleType(ScaleType.FIT_XY);
		//add file chooser ImageView to extra row
		trow_extra1.addView(chooseButton,parms);
		*/
		
		//init map button ImageView
		mapButton = new ImageView(this);
		mapButton.setImageDrawable(getResources().getDrawable(R.drawable.mapbutton_off));
		mapButton.setScaleType(ScaleType.FIT_XY);
		//add file chooser ImageView to extra row
		trow_extra1.addView(mapButton,parms);
		
		editButton = new ImageView(this);
		editButton.setImageDrawable(getResources().getDrawable(R.drawable.editbutton_off));
		editButton.setScaleType(ScaleType.FIT_XY);
		//add file chooser ImageView to extra row
		trow_extra1.addView(editButton,parms);
		
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
            	if(!getPlayButtonStatus())
            	{
            		//turn play button on w/ transition
	        		TransitionDrawable transition = (TransitionDrawable)
		    	            getResources().getDrawable(R.drawable.turn_play_on);
		    	    t.setImageDrawable(transition);
		    	    transition.startTransition(400);
		    	    setPlayButtonStatus(true);	//playButtonOn = true;
		    	    //turn map button off
		    	    mapButton.setImageDrawable(getResources().getDrawable(R.drawable.mapbutton_off));
            		setMapButtonStatus(false);
            		editButton.setImageDrawable(getResources().getDrawable(R.drawable.editbutton_off));
            		setEditButtonStatus(false);
            		CHOOSING = false;
            	}
            	else
            	{
            		t.setImageDrawable(getResources().getDrawable(R.drawable.playicon_off));
            		setPlayButtonStatus(false);
            		CHOOSING = false;
            		//playButtonOn = false;
            	}
            	
            	
            }
        });
		
		//register the stop button's click listener
		//responsible for changing this button's image on click
		stopButton.setOnClickListener(
			new OnClickListener() {
	            public void onClick(View v) {
	            	ImageView t = (ImageView) v;
	        		//flash stop button red w/ transition
	        		TransitionDrawable transition = (TransitionDrawable)
		    	            getResources().getDrawable(R.drawable.turn_stop_on);
		    	    t.setImageDrawable(transition);
		    	    transition.startTransition(400);
		    	    //pause music
		    	    stopAllButtons();
		    	    //turn map button off
		    	    mapButton.setImageDrawable(getResources().getDrawable(R.drawable.mapbutton_off));
	        		setMapButtonStatus(false);
	        		//turn edit button off
		    	    editButton.setImageDrawable(getResources().getDrawable(R.drawable.editbutton_off));
	        		setEditButtonStatus(false);
	        		//turn play button off
            		playButton.setImageDrawable(getResources().getDrawable(R.drawable.playicon_off));
            		setPlayButtonStatus(false);
            		CHOOSING = false;
	            }
			}
		);
		
		//register the map button's click listener
		//responsible for changing this button's image on click
		mapButton.setOnClickListener(new OnClickListener()
		{
            public void onClick(View v)
            {
            	ImageView t = (ImageView) v;

            	if(!getMapButtonStatus() && !CHOOSING){
            		//turn map button on w/ transition
                	TransitionDrawable transition;
	        		transition = (TransitionDrawable)
		    	            getResources().getDrawable(R.drawable.turn_map_on);
	            	t.setImageDrawable(transition);
		    	    transition.startTransition(400);
	        		setMapButtonStatus(true);
	        		//turn play button off
		    	    playButton.setImageDrawable(getResources().getDrawable(R.drawable.playicon_off));
            		setPlayButtonStatus(false);
            		editButton.setImageDrawable(getResources().getDrawable(R.drawable.editbutton_off));
            		setEditButtonStatus(false);
            		
            		//Open choose song menu
    	    	    showChooseFileDialog();
    	    	    CHOOSING = false;
            	} else {
            		t.setImageDrawable(getResources().getDrawable(R.drawable.mapbutton_off));
            		setMapButtonStatus(false);
            		CHOOSING = false;
            	}
	    	    
            }//end onClick
        });
		
		editButton.setOnClickListener(new OnClickListener()
		{
            public void onClick(View v)
            {
            	ImageView t = (ImageView) v;

            	if(!getEditButtonStatus()){
            		//turn map button on w/ transition
                	TransitionDrawable transition;
	        		transition = (TransitionDrawable)
		    	            getResources().getDrawable(R.drawable.turn_edit_on);
	            	t.setImageDrawable(transition);
		    	    transition.startTransition(400);
	        		setEditButtonStatus(true);
	        		//turn play button off
		    	    playButton.setImageDrawable(getResources().getDrawable(R.drawable.playicon_off));
            		setPlayButtonStatus(false);
            		mapButton.setImageDrawable(getResources().getDrawable(R.drawable.mapbutton_off));
            		setMapButtonStatus(false);
            		stopAllButtons();
    	    	    showSongSelectDialog();
    	    	    waitingId = true;
    	    	    CHOOSING = true;

            	} else {
            		t.setImageDrawable(getResources().getDrawable(R.drawable.editbutton_off));
            		setEditButtonStatus(false);
            		CHOOSING = false;
            	}
	    	    
            }//end onClick
        });
			   
	    //restore persistent state
		//if(!restored)
		//{
		//System.out.println("RESTORING!!");
		//restoreState();
		//restored = true;
		//}
		
	}//end onCreate
	
	
	//preloads song list
	protected void onStart()
	{
		super.onStart();
		ChooseFileDialog.setContext(this);
		chooseFileDialog = ChooseFileDialog.newInstance(R.string.chooseFileDialogTitle);
	}//end onStart
	
	//save preferences
	protected void onPause()
	{
		saveState();
		stopAllThreads();
		super.onPause();
	}//end onPause
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_button_matrix, menu);
		return true;
	}

	
	//load song list
	protected void onResume()
	{
		super.onResume();
		System.out.println("RESTORING!!");
		restoreState();
		restored = true;
		ChooseFileDialog.setContext(this);
		newFragment = ChooseFileDialog.newInstance(R.string.chooseFileDialogTitle);
	}
	
	//show the choose file dialog
	//set context and title, then show
	void showChooseFileDialog()
	{
		//ChooseFileDialog.setContext(this);
		//DialogFragment newFragment = ChooseFileDialog.newInstance(R.string.chooseFileDialogTitle);
	    chooseFileDialog.show(getFragmentManager(), "dialog");
	}
	
	void showSongSelectDialog() {
		/*// pass list of songs mapped for picking
		// gonna pick button ID for now
		
		SongSelectDialog.setContext(this);
		DialogFragment newFragment = SongSelectDialog.newInstance(R.string.songSelectDialogTitle);
		//newFragment = ChooseFileDialog.newInstance(R.string.chooseFileDialogTitle);
	    newFragment.show(getFragmentManager(), "dialog");
	    //with returned chosen song, move on to set time, etc
	    
	    //with returned info, call setters for audio player*/
	    
	}
	
	void showSongEditDialog() {
		SongEditDialog.initialize(editingButtonId, manager.getTrackLength(editingButtonId), manager.getStartTime(editingButtonId),
				manager.getEndTime(editingButtonId), manager.getPlaybackSpeed(editingButtonId), manager.getVolume(editingButtonId),
				manager.getBass(editingButtonId), manager.getTreble(editingButtonId));
		SongEditDialog.setContext(this);
		DialogFragment newFragment = SongEditDialog.newInstance(R.string.songEditDialogTitle);
	    newFragment.show(getFragmentManager(), "dialog");   
	}
	
/*	public void editAction(int buttonId) {
		System.out.println("in buttonmatrix, callback just got called");
		editingButtonId = buttonId;
		waitingId = false;
		System.out.println("test");
		showSongEditDialog();
	}*/
	
	//stops all button sounds
	//changes state of buttons as well
	public void stopAllButtons() {
		
		for (int i=0; i<buttonList.size();i++) {
			buttonList.get(i).stop();
		}
	}

	private void stopAllThreads() {
		for (int i=0; i<buttonList.size();i++) {
			manager.stopThread(i);
		}
	}
	
	//save persistent state
	public void saveState()
	{
		//get settings
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		Editor edit = settings.edit();
		//edit.clear();
		
		//store button state, mapped value, track path
		for(int i = 0; i < TOTAL_BUTTONS; i++)
		{
			/*//if not WAITING, store STOPPED
			if( buttonList.get(i).getState() != WAITING)
			{
				edit.putBool( PREFS_BUTTON_STATE + i, STOPPED );
				System.out.println(i + " was mapped.. in saveState");
				System.out.println( i +" path = " +  paths[i] + " in saveState");
				//manager.kill(i);
			}
			else
			{
				edit.putInt( PREFS_BUTTON_STATE + i, WAITING );
			}*/
			edit.putBoolean( PREFS_BUTTON_MAPPED + i, buttonList.get(i).getMapped() );
			if (buttonList.get(i).getMapped()) {
				FileOrRes fileorres = manager.getFileOrRes(i);
				System.out.println("Trying to save the fileorres for "+i);
				edit.putBoolean( PREFS_BUTTON_ISFILE + i , fileorres.isFile() );
				edit.putInt( PREFS_BUTTON_RESID + i , fileorres.getResid() );
				edit.putString( PREFS_BUTTON_FILENAME + i , fileorres.getAbsolutePath() );
			}
			//edit.putString( PREFS_BUTTON_FILENAME + i, paths[i] );
		}
		//commit settings
		edit.commit();	
	}
	
	//restore persistent state
	public void restoreState()
	{
		//paths = new String[TOTAL_BUTTONS];
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		//restore button state, mapped value, track path
		for(int i = 0; i < TOTAL_BUTTONS; i++)
		{
			//set button mappings depending on saved values
			if(settings.getBoolean(PREFS_BUTTON_MAPPED + i,false))
			{
				FileOrRes fileorres = null;
				if (settings.getBoolean(PREFS_BUTTON_ISFILE + i, true)) {
					File file = new File(settings.getString(PREFS_BUTTON_FILENAME + i, ""));
					if (file.canRead()) {
						fileorres = new FileOrRes(file);
					}
				}
				else {
					fileorres = new FileOrRes(settings.getInt(PREFS_BUTTON_RESID + i, 0));
				}
				if (fileorres != null) {
					buttonList.get(i).mapAction(fileorres);
				}
				//GestureListener listener = buttonList.get(i).getGestureListener();
				//listener.setTrack( new File(settings.getString(PREFS_BUTTON_TRACK + i, "")) );
				//paths[i] = settings.getString(PREFS_BUTTON_FILENAME + i, "");
			}
			/*else
			{
				buttonList.get(i).setState( WAITING );
			}
			buttonList.get(i).setState( WAITING );
			buttonList.get(i).setMapped( false );*/
		}
	}
	
	
	/*
	 * 
	 * Getters / Setters
	 * 
	 */
	
	public static void setMapButtonStatus(boolean status)
	{
		mapButtonOn = status;
	}
	
	
	public static boolean getMapButtonStatus()
	{
		return mapButtonOn; 
	}
	
	public static void setEditButtonStatus(boolean status)
	{
		editButtonOn = status;
	}
	
	
	public static boolean getEditButtonStatus()
	{
		return editButtonOn;
	}
	
	public static void setPlayButtonStatus(boolean status)
	{
		playButtonOn = status;
	}
	
	public static boolean getPlayButtonStatus()
	{
		return playButtonOn;
	}
	
	public static MediaPlayerManager getMediaPlayerManager()
	{
		return manager;
	}
	
	public static FileOrRes getChosenFile()
	{
		return chosenFile;
	}
	
	/*public static void setPath(int i, String f)
	{
		paths[i] = f;
	}*/



}//end class ButtonMatrix
