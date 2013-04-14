package com.joshuac.beatmatrix;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.*;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import com.joshuac.beatmatrix.SongEditDialog.OnSongEditSelectedListener;

public class SongEditDialog extends DialogFragment {

	private static Context context;
	
	private double start_time, end_time, speed;
	private float volume;
	private static int buttonId;
	
	//Text views
	private TextView SpeedText, VolumeText, StartText, EndText;
	
	OnSongEditSelectedListener mCallback;
	
    //Container Activity must implement this interface
	//fragment can deliver messages to an Activity by calling 
	//the onFileSelected() method (or other methods in this interface) 
	//using the mCallback instance of the OnChooseFileSelectedListener interface
    public interface OnSongEditSelectedListener {
        public void onEditInfoSelected(double start_time, double end_time, float volume, double speed);
    }
    
    //creates a new instance of the dialog
	public static SongEditDialog newInstance(int title) {
		SongEditDialog frag = new SongEditDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }
	
	public static void setContext(Context c){
		context = c;
	}
	
	public static void setButtonId(int Id) {
		buttonId = Id;
	}
	
	private OnSeekBarChangeListener startSeekBarListener = new OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
			if (start_time > end_time) {
				start_time = end_time;
			} else {
				start_time = seekBar.getProgress();
			}
			System.out.println("Start time: " + start_time);
			StartText.setText("Start Time: " + start_time);
		}
		public void onStartTrackingTouch(SeekBar seekBar) {
		}
		public void onStopTrackingTouch(SeekBar seekBar) {
			seekBar.setProgress((int)start_time);
		}
	};
	private OnSeekBarChangeListener endSeekBarListener = new OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
			if (end_time < start_time) {
				end_time = start_time;
			} else {
				end_time = seekBar.getProgress();
			}
			System.out.println("End time: " + end_time);
			EndText.setText("End Time: " + end_time);
		}
		public void onStartTrackingTouch(SeekBar seekBar) {
		}
		public void onStopTrackingTouch(SeekBar seekBar) {
			seekBar.setProgress((int)end_time);
		}
	};
	private OnSeekBarChangeListener volSeekBarListener = new OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
			volume = seekBar.getProgress();
			System.out.println("Volume: " + volume);
			VolumeText.setText("Volume: " + volume);
		}
		public void onStartTrackingTouch(SeekBar seekBar) {
		}
		public void onStopTrackingTouch(SeekBar seekBar) {
			seekBar.setProgress((int)volume);
		}
	};
	private OnSeekBarChangeListener speedSeekBarListener = new OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
			speed = seekBar.getProgress()/50;
			System.out.println("Speed: " + speed);
			SpeedText.setText("Speed: " + speed);
		}
		public void onStartTrackingTouch(SeekBar seekBar) {
		}
		public void onStopTrackingTouch(SeekBar seekBar) {
			seekBar.setProgress((int)speed*50);
		}
	};
	
		
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
    	
    	System.out.println("Called onCreateDialog");
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	LayoutInflater inflater = getActivity().getLayoutInflater();
    	 View v = inflater.inflate(R.layout.song_edit_layout, null);   	 
	  
       //Set start listener
 	   SeekBar StartSeek = (SeekBar)v.findViewById(R.id.StartSeek);
 	   System.out.println("about to set progress");
 	   start_time = 0;
 	   StartSeek.setProgress((int)start_time);
 	   System.out.println("set progress");
 	   StartSeek.setOnSeekBarChangeListener(startSeekBarListener);
 	   System.out.println("set listener");
 	   StartText = (TextView) v.findViewById(R.id.StartText);
 	   StartText.setText("Start Time: " + start_time);
  	 
 	   //Set end listener
 	   SeekBar EndSeek = (SeekBar)v.findViewById(R.id.EndSeek);
 	   end_time = 100;
 	   EndSeek.setProgress((int)end_time);
 	   EndSeek.setOnSeekBarChangeListener(endSeekBarListener);
 	   EndText = (TextView) v.findViewById(R.id.EndText);
 	   EndText.setText("End Time: " + end_time);	// !!!!!! Fix so it inits at actual song end time!

 	   //Set volume listener
 	   SeekBar VolSeek = (SeekBar)v.findViewById(R.id.VolSeek);
 	   volume = 1;
 	   VolSeek.setProgress((int)volume);
 	   VolSeek.setOnSeekBarChangeListener(volSeekBarListener);
 	   VolumeText = (TextView) v.findViewById(R.id.VolumeText);
 	   VolumeText.setText("Volume: " + volume);
 	   
 	   //Set speed listener
 	   SeekBar SpeedSeek = (SeekBar)v.findViewById(R.id.SpeedSeek);
 	   speed = 1;
 	   SpeedSeek.setProgress((int)speed);
 	   SpeedSeek.setOnSeekBarChangeListener(speedSeekBarListener);
 	   SpeedText = (TextView) v.findViewById(R.id.SpeedText);
 	   SpeedText.setText("Speed: " + speed);
 	   
        builder.setTitle(R.string.songEditDialogTitle)
    		.setView(v/*inflater.inflate(R.layout.song_edit_layout, null)*/)
    	    // Add action buttons
    		.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
//            	   LayoutInflater inflater = (LayoutInflater) context
  //          	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            	   mCallback.onEditInfoSelected(start_time, end_time, volume, speed);
               }
           })
           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                   SongEditDialog.this.getDialog().cancel();
               }
           });

        return builder.create();
    }//end onCreateDialog

    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnSongEditSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSongEditSelectedListener");
        }
    }//end onAttach
    
}//end ChooseFileDialog
